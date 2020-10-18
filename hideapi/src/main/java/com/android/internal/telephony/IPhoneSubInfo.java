package com.android.internal.telephony;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

/**
 * Interface used to retrieve various phone-related subscriber information.
 *
 */
public interface IPhoneSubInfo extends IInterface {
    public abstract class Stub extends Binder implements IPhoneSubInfo {
        public static IPhoneSubInfo asInterface(IBinder binder) {
            throw new IllegalArgumentException("Stub!");
        }

        @Override
        public IBinder asBinder() {
            throw new IllegalArgumentException("Stub!");
        }
    }

    String getDeviceId(String callingPackage) throws RemoteException;
    String getNaiForSubscriber(int subId, String callingPackage) throws RemoteException;
    String getDeviceIdForPhone(int phoneId, String callingPackage) throws RemoteException;
    String getImeiForSubscriber(int subId, String callingPackage) throws RemoteException;
    String getDeviceSvn(String callingPackage) throws RemoteException;
    String getDeviceSvnUsingSubId(int subId, String callingPackage) throws RemoteException;
    String getSubscriberId(String callingPackage) throws RemoteException;
    String getSubscriberIdForSubscriber(int subId, String callingPackage) throws RemoteException;
    String getIccSerialNumber(String callingPackage) throws RemoteException;
    String getIccSerialNumberForSubscriber(int subId, String callingPackage) throws RemoteException;
    String getMsisdn(String callingPackage) throws RemoteException;
    String getMsisdnForSubscriber(int subId, String callingPackage) throws RemoteException;
}