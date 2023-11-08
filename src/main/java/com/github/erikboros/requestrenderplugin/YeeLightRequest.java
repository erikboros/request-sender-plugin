package com.github.erikboros.requestrenderplugin;

import com.moppletop.yeelight.api.YeeApi;
import com.moppletop.yeelight.api.YeeApiBuilder;
import com.moppletop.yeelight.api.model.YeeDuration;
import com.moppletop.yeelight.api.model.YeeLight;

import java.util.List;
import java.util.stream.Collectors;

public class YeeLightRequest {

    private static YeeApi api = null;

    private static YeeApi getApi() {
        if (api == null) {
            // All values in this example are the defaults
            YeeApiBuilder builder = new YeeApiBuilder();
            // When calling .build(), if this is true. api.discoverLights with a timeout of 1000ms
            // will be called automatically, set to false if you want to handle this yourself
            builder.autoDiscovery(true);
            // Finalises the build and starts the UDP discovery thread
            api = builder.build();
        }
        return api;
    }

    public static String test() {
        StringBuilder ids = new StringBuilder();
        for (YeeLight l : getApi().getLights()) {

            //TODO add set force-power on
            getApi().setPower(l.getId(), true, YeeDuration.instant());
            getApi().setBrightness(l.getId(), 50, YeeDuration.instant());
            getApi().setRgb(l.getId(), 0, 255, 0, YeeDuration.instant());
            ids.append(l.getId());
            ids.append(l.getName());
        }

        return ids.toString();
    }

    public static String sendSuccess(List<Integer> ids) {
        return sendColor(ids, 0, 255, 0);
    }

    public static String sendFailed(List<Integer> ids) {
        return sendColor(ids, 255, 0, 0);
    }

    private static String sendColor(List<Integer> ids, int r, int g, int b) {
        List<YeeLight> foundIds = getApi().getLights().stream()
                .filter(l -> ids.contains(l.getId())).toList();

        for (YeeLight l : foundIds) {
            //TODO separate thread + sleeps
            //save current state
            boolean tmpPowered = l.isPowered();
            int tmpBrightness = l.getBrightness();
            int tmpRgb = l.getRgb();
//            l.getHue();
//            l.getSaturation();

            //flash color
            //TODO if force on is true
            getApi().setPower(l.getId(), true, YeeDuration.instant()); // TODO only if not already powered
            getApi().setRgb(l.getId(), r, g, b, YeeDuration.millis(250));
            getApi().setBrightness(l.getId(), 50, YeeDuration.millis(250));

            //TODO restore state
//            getApi().setRgb(l.getId(), tmpRgb, YeeDuration.seconds(3));
//            getApi().setBrightness(l.getId(), tmpBrightness, YeeDuration.seconds(3));
//            getApi().setPower(l.getId(), tmpPowered, YeeDuration.seconds(3)); // TODO only if wasnt powered
        }

//        return foundIds.stream().map(l -> (l.getId()) + ": " + l.getName()).collect(Collectors.joining());
        return foundIds.stream().map(YeeLight::getId).map(Object::toString).collect(Collectors.joining());
    }
}
