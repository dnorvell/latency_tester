package com.kony.latencytester.application;

import com.kony.latencytester.service.BaseService;
import com.kony.latencytester.service.ServiceType;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dnorvell on 9/1/16.
 */
public abstract class BaseApplication {

    private static Map<ServiceType, WeakReference<BaseService>> sServiceMap = new HashMap<>();

    public static void registerService(BaseService _service) {
        if (_service == null) {
            return;
        }

        sServiceMap.put(_service.getServiceType(), new WeakReference<>(_service));

    }

    public static void unRegisterService(BaseService _service) {
        if (_service == null) {
            return;
        }

        sServiceMap.remove(_service.getServiceType());

    }

    public static BaseService getService(ServiceType _serviceType) {
        WeakReference<BaseService> serviceWeakReference = sServiceMap.get(_serviceType);
        if (serviceWeakReference != null) {
            return serviceWeakReference.get();
        }

        return null;

    }

}
