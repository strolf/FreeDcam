package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_MTK;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_QcomM;

/**
 * Created by troop on 31.05.2016.
 */
public abstract class AbstractDevice
{
    protected Handler uihandler;
    protected Camera.Parameters parameters;
    protected CameraHolderApi1 cameraHolder;
    protected CamParametersHandler camParametersHandler;

    public AbstractDevice(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler)
    {
        this.uihandler = uihandler;
        this.parameters = parameters;
        this.cameraHolder = cameraHolder;
        this.camParametersHandler = camParametersHandler;
    }

    public abstract AbstractManualParameter getExposureTimeParameter();
    public abstract AbstractManualParameter getIsoParameter();
    public abstract AbstractManualParameter getManualFocusParameter();
    public abstract AbstractManualParameter getCCTParameter();
}
