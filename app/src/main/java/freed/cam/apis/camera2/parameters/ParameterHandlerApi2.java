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

package freed.cam.apis.camera2.parameters;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Key;
import android.os.Build.VERSION_CODES;

import com.huawei.camera2ex.CaptureRequestEx;

import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModuleParameters;
import freed.cam.apis.basecamera.parameters.modes.ToneMapChooser;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.FocusHandler;
import freed.cam.apis.camera2.parameters.huawei.HuaweiAeHandler;
import freed.cam.apis.camera2.parameters.manual.BurstApi2;
import freed.cam.apis.camera2.parameters.manual.ManualFocus;
import freed.cam.apis.camera2.parameters.manual.ManualToneMapCurveApi2;
import freed.cam.apis.camera2.parameters.manual.ZoomApi2;
import freed.cam.apis.camera2.parameters.modes.AeLockModeApi2;
import freed.cam.apis.camera2.parameters.modes.AeTargetRangeApi2;
import freed.cam.apis.camera2.parameters.modes.BaseModeApi2;
import freed.cam.apis.camera2.parameters.modes.DualCameraModeHuaweiApi2;
import freed.cam.apis.camera2.parameters.modes.FocusPeakModeApi2;
import freed.cam.apis.camera2.parameters.modes.JpegQualityModeApi2;
import freed.cam.apis.camera2.parameters.modes.PictureFormatParameterApi2;
import freed.cam.apis.camera2.parameters.modes.PictureSizeModeApi2;
import freed.cam.apis.camera2.parameters.modes.VideoProfilesApi2;
import freed.utils.Log;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ParameterHandlerApi2 extends AbstractParameterHandler
{
    private final String TAG = ParameterHandlerApi2.class.getSimpleName();


    private CameraHolderApi2 cameraHolder;

    public ParameterHandlerApi2(CameraWrapperInterface wrapper)
    {
        super(wrapper);
    }


    public void Init()
    {
        this.cameraHolder = (CameraHolderApi2) cameraUiWrapper.getCameraHolder();
        List<Key<?>> keys = cameraHolder.characteristics.getAvailableCaptureRequestKeys();
        for (int i = 0; i< keys.size(); i++)
        {
            Log.d(TAG, keys.get(i).getName());
        }
        Module = new ModuleParameters(cameraUiWrapper, appSettingsManager);
        if (appSettingsManager.flashMode.isSupported())
            FlashMode = new BaseModeApi2(cameraUiWrapper,appSettingsManager.flashMode,CaptureRequest.FLASH_MODE);
        if (appSettingsManager.sceneMode.isSupported())
            SceneMode = new BaseModeApi2(cameraUiWrapper,appSettingsManager.sceneMode,CaptureRequest.CONTROL_SCENE_MODE);
        if (appSettingsManager.antiBandingMode.isSupported())
            AntiBandingMode = new BaseModeApi2(cameraUiWrapper,appSettingsManager.antiBandingMode, CaptureRequest.CONTROL_AE_ANTIBANDING_MODE);
        if (appSettingsManager.colorMode.isSupported())
            ColorMode = new BaseModeApi2(cameraUiWrapper,appSettingsManager.colorMode,CaptureRequest.CONTROL_EFFECT_MODE);
        if (appSettingsManager.controlMode.isSupported())
            ControlMode = new BaseModeApi2(cameraUiWrapper,appSettingsManager.controlMode,CaptureRequest.CONTROL_MODE);
        if (appSettingsManager.denoiseMode.isSupported())
            Denoise = new BaseModeApi2(cameraUiWrapper,appSettingsManager.denoiseMode,CaptureRequest.NOISE_REDUCTION_MODE);
        if (appSettingsManager.edgeMode.isSupported())
            EdgeMode = new BaseModeApi2(cameraUiWrapper,appSettingsManager.edgeMode,CaptureRequest.EDGE_MODE);
        if (appSettingsManager.opticalImageStabilisation.isSupported())
            oismode = new BaseModeApi2(cameraUiWrapper,appSettingsManager.opticalImageStabilisation,CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE);
        if (appSettingsManager.focusMode.isSupported()) {
            FocusMode = new BaseModeApi2(cameraUiWrapper, appSettingsManager.focusMode, CaptureRequest.CONTROL_AF_MODE);
            FocusMode.addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).focusModeListner);
        }
        if (appSettingsManager.hotpixelMode.isSupported())
            HotPixelMode = new BaseModeApi2(cameraUiWrapper,appSettingsManager.hotpixelMode,CaptureRequest.HOT_PIXEL_MODE);
        if (appSettingsManager.ae_TagetFPS.isSupported())
            ae_TargetFPS = new AeTargetRangeApi2(cameraUiWrapper,appSettingsManager.ae_TagetFPS,CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE);

        if (appSettingsManager.dualPrimaryCameraMode.isSupported() && !appSettingsManager.getIsFrontCamera())
        {
            dualPrimaryCameraMode = new DualCameraModeHuaweiApi2(cameraUiWrapper,appSettingsManager.dualPrimaryCameraMode, CaptureRequestEx.HUAWEI_DUAL_SENSOR_MODE);
        }
        JpegQuality = new JpegQualityModeApi2(cameraUiWrapper);

        WbHandler wbHandler = new WbHandler(cameraUiWrapper);
        CCT = wbHandler.manualWbCt;
        WhiteBalanceMode =wbHandler.whiteBalanceApi2;
        //dont make that avail for the ui its only internal used
        //ColorCorrectionMode = colorCorrectionMode;

        //AE mode start
        AeHandler aeHandler;
        if (appSettingsManager.useHuaweiCam2Extension.getBoolean())
            aeHandler = new HuaweiAeHandler(cameraUiWrapper);
        else {
            aeHandler = new AeHandler(cameraUiWrapper);
            //not used by huawei
            ExposureMode = aeHandler.aeModeApi2;
            ExposureMode.addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).aeModeListner);
        }
        //pass stuff to the parameterhandler that it get used by the ui

        ManualShutter = aeHandler.manualExposureTimeApi2;
        ManualExposure = aeHandler.manualExposureApi2;
        ManualIso = aeHandler.manualISoApi2;
        //ae mode end
        PictureSize = new PictureSizeModeApi2(cameraUiWrapper);


        //MF
        ManualFocus mf = new ManualFocus(cameraUiWrapper);
        ManualFocus = mf;

        //MF END

        ManualToneMapCurveApi2 manualToneMapCurveApi2 = new ManualToneMapCurveApi2(cameraUiWrapper);
        ManualContrast = manualToneMapCurveApi2.contrast;
        ManualBrightness = manualToneMapCurveApi2.brightness;
        black = manualToneMapCurveApi2.black;
        shadows = manualToneMapCurveApi2.shadowsp;
        midtones = manualToneMapCurveApi2.midtonesp;
        highlights = manualToneMapCurveApi2.highlightsp;
        white = manualToneMapCurveApi2.whitep;

        ToneMapMode = new BaseModeApi2(cameraUiWrapper,appSettingsManager.toneMapMode,CaptureRequest.TONEMAP_MODE);
        ToneMapMode.addEventListner(manualToneMapCurveApi2);

        PictureFormat = new PictureFormatParameterApi2(cameraUiWrapper, appSettingsManager.pictureFormat, null);

        ExposureLock = new AeLockModeApi2(cameraUiWrapper);




        Burst = new BurstApi2(cameraUiWrapper);
        Focuspeak = new FocusPeakModeApi2(cameraUiWrapper);
        VideoProfiles = new VideoProfilesApi2(cameraUiWrapper);
        matrixChooser = new MatrixChooserParameter(appSettingsManager.getMatrixesMap(),appSettingsManager);
        tonemapChooser = new ToneMapChooser(appSettingsManager.getToneMapProfiles(), appSettingsManager);
        Zoom = new ZoomApi2(cameraUiWrapper);
        SetAppSettingsToParameters();
    }

    @Override
    public void SetFocusAREA(Rect focusAreas) {

    }



    @Override
    public void SetPictureOrientation(int orientation)
    {
        if (appSettingsManager.orientationhack.getBoolean())
        {
            int or = orientation +180;
            if (or >360)
                or = or - 360;
            orientation = or;
        }
        if (cameraHolder == null || cameraHolder.isWorking)
            return;
        try
        {
            Log.d(TAG, "Set Orientation to:" + orientation);
            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.JPEG_ORIENTATION, orientation);
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Override
    public float[] getFocusDistances()
    {
        return cameraHolder.GetFocusRange();
    }

    @Override
    public float getCurrentExposuretime() {
        return 0;
    }

    @Override
    public int getCurrentIso() {
        return 0;
    }


}
