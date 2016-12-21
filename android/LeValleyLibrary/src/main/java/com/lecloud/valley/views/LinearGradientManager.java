package com.lecloud.valley.views;


import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import static com.lecloud.valley.common.Constants.REACT_CLASS_LINEAR_GRADIENT_VIEW;

/**
 * Created by raojia on 2016/12/7.
 */
public class LinearGradientManager extends SimpleViewManager<LinearGradientView> {

    public static final String REACT_CLASS = REACT_CLASS_LINEAR_GRADIENT_VIEW; //"BVLinearGradient";
    public static final String PROP_COLORS = "colors";
    public static final String PROP_LOCATIONS = "locations";
    public static final String PROP_START_POS = "start";
    public static final String PROP_END_POS = "end";
    public static final String PROP_BORDER_RADII = "borderRadii";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected LinearGradientView createViewInstance(ThemedReactContext context) {
        return new LinearGradientView(context);
    }

    @ReactProp(name=PROP_COLORS)
    public void setColors(LinearGradientView gradientView, ReadableArray colors) {
        gradientView.setColors(colors);
    }

    @ReactProp(name=PROP_LOCATIONS)
    public void setLocations(LinearGradientView gradientView, ReadableArray locations) {
        if (locations != null) {
            gradientView.setLocations(locations);
        }
    }

    @ReactProp(name=PROP_START_POS)
    public void setStartPosition(LinearGradientView gradientView, ReadableArray startPos) {
        gradientView.setStartPosition(startPos);
    }

    @ReactProp(name=PROP_END_POS)
    public void setEndPosition(LinearGradientView gradientView, ReadableArray endPos) {
        gradientView.setEndPosition(endPos);
    }

    // temporary solution until following issue is resolved:
    // https://github.com/facebook/react-native/issues/3198
    @ReactProp(name=PROP_BORDER_RADII)
    public void setBorderRadii(LinearGradientView gradientView, ReadableArray borderRadii) {
        gradientView.setBorderRadii(borderRadii);
    }
}