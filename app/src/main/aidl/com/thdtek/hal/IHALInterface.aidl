// IHALInterface.aidl
package com.thdtek.hal;

// Declare any non-default types here with import statements
import android.content.Context;

interface IHALInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   //void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
   //         double aDouble, String aString);

    /**
     * GPIO Interface
     */
    long GPIO_Init(String provider, String version);
    long GPIO_WritePin(int gpioNumber,int level);
     long GPIO_UnInit();

    /**
     * Card reader
     */
     long   IReader_Init(String provider, String version);
     String IReader_GetIC_ID(String icardType);
     String IReader_GetID_Text();
     long   IReader_UnInit();

     /**
     * Light interface
     */
     long Light_Init(String provider, String version);
     long Light_UnInit();
     long Light_OnAux();
     long Light_OffAux();

     /**
     * Fan interface
     */
     long Fan_Init(String provider, String version);
     long Fan_UnInit();
     long Fan_On();
     long Fan_Off();
}
