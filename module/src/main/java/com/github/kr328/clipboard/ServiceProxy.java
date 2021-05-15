package com.github.kr328.clipboard;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.IServiceManager;
import android.os.ServiceManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@SuppressWarnings("JavaReflectionMemberAccess")
@SuppressLint("DiscouragedPrivateApi")
public abstract class ServiceProxy implements InvocationHandler {
    private IServiceManager original;

    public synchronized void install() throws ReflectiveOperationException {
        if (original != null) return;

        Method method = ServiceManager.class.getDeclaredMethod("getIServiceManager");
        Field field = ServiceManager.class.getDeclaredField("sServiceManager");

        method.setAccessible(true);
        field.setAccessible(true);

        original = (IServiceManager) method.invoke(null);
        field.set(null, Proxy.newProxyInstance(
                ServiceProxy.class.getClassLoader(),
                new Class[]{IServiceManager.class},
                this
        ));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "addService": {
                if (args.length < 2) return method.invoke(original, args);
                if (!(args[0] instanceof String)) return method.invoke(original, args);
                if (!(args[1] instanceof IBinder)) return method.invoke(original, args);

                final String name = (String) args[0];
                final IBinder service = (IBinder) args[1];

                args[1] = onAddService(name, service);

                return method.invoke(original, args);
            }
            case "getService":
                if (args.length < 1) return method.invoke(original, args);
                if (!(args[0] instanceof String)) return method.invoke(original, args);

                final String n = (String) args[0];
                final Object s = method.invoke(original, args);

                if (!(s instanceof IBinder)) return s;

                return onGetService(n, (IBinder) s);
        }

        return method.invoke(original, args);
    }

    protected IBinder onAddService(String name, IBinder service) {
        return service;
    }

    protected IBinder onGetService(String name, IBinder service) {
        return service;
    }
}
