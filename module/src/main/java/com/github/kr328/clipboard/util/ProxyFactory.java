package com.github.kr328.clipboard.util;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import com.github.kr328.clipboard.shared.Log;

import java.io.FileDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ProxyFactory {
    public static Binder instance(Binder original, Binder replaced) throws ReflectiveOperationException {
        final StubBinder stubBinder = new StubBinder();
        final Object stubProxy = replaced.getClass()
                .getMethod("asInterface", IBinder.class)
                .invoke(null, stubBinder);

        if (stubProxy == null) {
            throw new NoSuchMethodException("asInterface invalid");
        }

        for (Method method : replaced.getClass().getMethods()) {
            TransactHook hook = method.getAnnotation(TransactHook.class);

            if (hook == null)
                continue;

            try {
                final int[] predefined = hook.value();
                if (predefined.length == 0) {
                    stubProxy.getClass().getMethod(method.getName(), method.getParameterTypes())
                            .invoke(stubProxy, generateDefaultArgs(method.getParameterTypes()));
                } else {
                    for (int code : predefined) {
                        stubBinder.transactedCodes.add(code);
                    }
                }
            } catch (ReflectiveOperationException e) {
                Log.w("");
            }
        }

        Log.i("hook " + stubBinder.transactedCodes);

        return new ProxyBinder(original, replaced, stubBinder.transactedCodes);
    }

    private static Object[] generateDefaultArgs(Class<?>[] types) {
        return Stream.of(types).map((type) -> {
            switch (type.getName()) {
                case "boolean":
                    return false;
                case "short":
                    return (short) 0;
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

    private static final class StubBinder implements IBinder {
        private final Set<Integer> transactedCodes = new HashSet<>();

        @Override
        public String getInterfaceDescriptor() {
            return null;
        }

        @Override
        public boolean pingBinder() {
            return true;
        }

        @Override
        public boolean isBinderAlive() {
            return true;
        }

        @Override
        public IInterface queryLocalInterface(String descriptor) {
            return null;
        }

        @Override
        public void dump(FileDescriptor fd, String[] args) {
            throw new IllegalArgumentException("not implement");
        }

        @Override
        public void dumpAsync(FileDescriptor fd, String[] args) {
            throw new IllegalArgumentException("not implement");
        }

        @Override
        public boolean transact(int code, Parcel data, Parcel reply, int flags) {
            transactedCodes.add(code);

            return true;
        }

        @Override
        public void linkToDeath(DeathRecipient recipient, int flags) {
            throw new IllegalArgumentException("not implement");
        }

        @Override
        public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
            throw new IllegalArgumentException("not implement");
        }
    }

    private static class ProxyBinder extends Binder {
        private final Binder original;
        private final Binder replaced;
        private final Set<Integer> codes;

        public ProxyBinder(Binder original, Binder replaced, Set<Integer> codes) {
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
            return original.getInterfaceDescriptor();
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
            original.linkToDeath(deathRecipient, i);
        }

        @Override
        public boolean unlinkToDeath(DeathRecipient deathRecipient, int i) {
            return original.unlinkToDeath(deathRecipient, i);
        }
    }
}
