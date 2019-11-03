package com.github.kr328.clipboard.proxy;

import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;
import com.github.kr328.clipboard.Constants;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class LocalInterfaceProxy {
    public static <Interface extends IInterface> IBinder createInterfaceProxyBinder(Interface original, String interfaceName, InterfaceCallback<Interface> callback) {
        IBinder originalBinder = original.asBinder();

        InvocationHandler interfaceInvocationHandler = (Object thiz, Method method, Object[] args) -> callback.onCalled(original, (Interface) thiz, method, args);

        InvocationHandler binderInvocationHandler = (Object thiz, Method method, Object[] args) -> {
            try {
                if (method.getName().equals("queryLocalInterface")) {
                    //Log.i(Constants.TAG ,"queryLocalInterface " + args[0] + " == " + interfaceName);
                    if (interfaceName.equals(args[0]))
                        return Proxy.newProxyInstance(LocalInterfaceProxy.class.getClassLoader(), new Class[]{Class.forName(interfaceName)}, interfaceInvocationHandler);
                }
            } catch (Exception ignored) {
                Log.w(Constants.TAG, "Proxy " + original.getClass().getName() + " failure.");
            }

            return method.invoke(originalBinder, args);
        };

        return (IBinder) Proxy.newProxyInstance(LocalInterfaceProxy.class.getClassLoader(), new Class[]{IBinder.class}, binderInvocationHandler);
    }

    public static <Interface extends IInterface> Interface createInterfaceProxy(Interface original, Class<?>[] proxy, InterfaceCallback<Interface> callback) {
        return (Interface) Proxy.newProxyInstance(original.getClass().getClassLoader(), proxy, (replaced, method, args) -> {
            try {
                return callback.onCalled(original, (Interface) replaced, method, args);
            } catch (InvocationTargetException e) {
                throw Objects.requireNonNull(e.getCause());
            }
        });
    }

    public interface InterfaceCallback<Interface extends IInterface> {
        Object onCalled(Interface original, Interface replaced, Method method, Object[] args) throws Throwable;
    }
}
