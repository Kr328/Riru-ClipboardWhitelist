package com.github.kr328.clipboard.proxy;

import android.os.Binder;
import android.util.Log;
import com.github.kr328.clipboard.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

public class ProxyBinderFactory {
    public static <T extends Binder> Binder createProxyBinder(Binder original, T replace) throws ReflectiveOperationException {
        Set<Integer> transactCodes = exportTransactCodes(replace.getClass());
        return new ProxyBinder(original, (o, code, data, reply, flags) -> {
            if (transactCodes.contains(code))
                return replace.transact(code, data, reply, flags);
            return o.transact(code, data, reply, flags);
        });
    }

    private static Set<Integer> exportTransactCodes(Class<? extends Binder> clazz) throws ReflectiveOperationException {
        TreeSet<Integer> result = new TreeSet<>();
        TransactCodeExporter exporter = new TransactCodeExporter(clazz);

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(ReplaceTransact.class) != null) {
                int transactCode = exporter.export(method.getName(), method.getParameterTypes());
                if (transactCode != -1)
                    result.add(transactCode);
                else
                    Log.w(Constants.TAG, "transact code for " + method.toString() + " not found");
            }
        }

        return result;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ReplaceTransact {
    }
}
