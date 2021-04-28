package com.github.kr328.clipboard;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.github.kr328.clipboard.shared.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class ProxyFactory {
    public static Binder instance(IBinder original, Binder replaced) throws ReflectiveOperationException {
        final TreeSet<Integer> codes = new TreeSet<>();
        final Binder stub = new Binder() {
            @Override
            protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
                codes.add(code);

                return true;
            }
        };
        final Object impl = replaced.getClass().getMethod("asInterface", IBinder.class).invoke(null, stub);

        for (Method method : replaced.getClass().getMethods()) {
            TransactHook hook = method.getAnnotation(TransactHook.class);

            if (hook == null)
                continue;

            try {
                final int[] code = hook.value();
                final int count = codes.size();

                if (code.length > 0) {
                    for (int c : code) {
                        codes.add(c);
                    }
                } else {
                    reflectCode(impl, method);
                }

                if (codes.size() == count)
                    throw new NoSuchMethodException("implement of " + method.toGenericString() + " not found.");
            } catch (ReflectiveOperationException e) {
                Log.w(Constants.TAG, "hook " + original.getClass().toGenericString() + " failure", e);
            }
        }

        Log.i(Constants.TAG, "hook " + codes.toString());

        return new ProxyBinder(original, replaced, codes);
    }

    private static void reflectCode(Object stubImpl, Method method) throws ReflectiveOperationException {
        stubImpl.getClass().getMethod(method.getName(), method.getParameterTypes())
                .invoke(stubImpl, generateDefaultArgs(method.getParameterTypes()));
    }

    private static Object[] generateDefaultArgs(Class<?>[] types) {
        return Stream.of(types).map((type) -> {
            switch (type.getName()) {
                case "int":
                    return 0;
                case "long":
                    return 0L;
                case "float":
                    return 0.0f;
                case "double":
                    return 0.0d;
                case "char":
                    return '\0';
                case "byte":
                    return (byte) 0;
                default:
                    return null;
            }
        }).toArray();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface TransactHook {
        int[] value() default {};
    }

    public static class ProxyBinder extends Binder {
        private final IBinder original;
        private final IBinder replaced;
        private final Set<Integer> codes;

        public ProxyBinder(IBinder original, IBinder replaced, Set<Integer> codes) {
            this.original = original;
            this.replaced = replaced;
            this.codes = codes;
        }

        @Override
        public IInterface queryLocalInterface(String descriptor) {
            return null;
        }

        @Override
        public void attachInterface(IInterface owner, String descriptor) {
        }

        @Override
        public String getInterfaceDescriptor() {
            try {
                return original.getInterfaceDescriptor();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public boolean pingBinder() {
            return original.pingBinder();
        }

        @Override
        public boolean isBinderAlive() {
            return original.isBinderAlive();
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (codes.contains(code))
                return replaced.transact(code, data, reply, flags);

            return original.transact(code, data, reply, flags);
        }

        @Override
        public void linkToDeath(DeathRecipient deathRecipient, int i) {
            try {
                original.linkToDeath(deathRecipient, i);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public boolean unlinkToDeath(DeathRecipient deathRecipient, int i) {
            return original.unlinkToDeath(deathRecipient, i);
        }
    }
}
