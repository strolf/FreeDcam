/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera1.parameters.manual;

/**
 * Created by George on 1/21/2015.
 */

import android.hardware.Camera.Parameters;
import android.os.Build.VERSION;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.modules.I_ModuleEvent;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.DeviceUtils.Devices;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

public class BurstManualParam extends BaseManualParameter
{

    final String TAG = BurstManualParam.class.getSimpleName();

    public BurstManualParam(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, "", "", "", cameraUiWrapper,1);

        if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.GetAppSettingsManager().getDevice()== Devices.LG_G4
                || parameters.get(KEYS.NUM_SNAPS_PER_SHUTTER) != null
                || parameters.get(KEYS.SNAPSHOT_BURST_NUM) != null
                || parameters.get(KEYS.BURST_NUM)!= null)
        {
            isSupported = true;
            int max = 10;
            if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                    ||  cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2)
                max =  7;
            else if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W)
                max =  9;
            else if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W)
                if (VERSION.SDK_INT < 23)
                    max =  6;
                else
                    max =  10;
            else if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G4)
                max =  6;
            stringvalues = createStringArray(2,max,1);
            currentInt = 0;
        }
    }

    @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(KEYS.OFF);
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public int GetValue()
    {
        return currentInt;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;

        if (parameters.get(KEYS.NUM_SNAPS_PER_SHUTTER) != null
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W)
        {
            if (currentInt == 0)
                parameters.set(KEYS.NUM_SNAPS_PER_SHUTTER, 1+"");
            else
                parameters.set(KEYS.NUM_SNAPS_PER_SHUTTER, stringvalues[currentInt]);
            Logger.d(TAG, KEYS.NUM_SNAPS_PER_SHUTTER+ stringvalues[currentInt]);

        }
        if (parameters.get(KEYS.BURST_NUM)!=null)
        {
            if (currentInt == 0)
                parameters.set(KEYS.SNAPSHOT_BURST_NUM, String.valueOf(0));
            else
                parameters.set(KEYS.SNAPSHOT_BURST_NUM, stringvalues[currentInt]);
            Logger.d(TAG, KEYS.SNAPSHOT_BURST_NUM+ stringvalues[currentInt]);
        }
        else if(parameters.get(KEYS.BURST_NUM) != null)
        {
            if (valueToSet == 0)
                parameters.set(KEYS.BURST_NUM, String.valueOf(0));
            else
                parameters.set(KEYS.BURST_NUM, stringvalues[currentInt]);
            Logger.d(TAG, KEYS.BURST_NUM+ stringvalues[currentInt]);
        }

        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);

    }

    @Override
    public String GetStringValue() {
        return stringvalues[currentInt];
    }

    @Override
    public I_ModuleEvent GetModuleListner() {
        return moduleListner;
    }

    private final I_ModuleEvent moduleListner =new I_ModuleEvent() {
        @Override
        public void ModuleChanged(String module)
        {
            if ((module.equals(KEYS.MODULE_VIDEO) || module.equals(KEYS.MODULE_HDR)) && isSupported)
                ThrowBackgroundIsSupportedChanged(false);
            else if ((module.equals(KEYS.MODULE_PICTURE)
                    || module.equals(KEYS.MODULE_INTERVAL)
                    )&& isSupported)
            {
                ThrowBackgroundIsSupportedChanged(true);
            }
        }
    };

}