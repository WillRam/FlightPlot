package me.drton.flightplot.processors;

import me.drton.jmavlib.conversion.RotationConversion;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ton on 05.01.15.
 */
public class EulerFromQuaternion extends PlotProcessor {
    private String[] param_Fields;
    private double param_Scale;
    private boolean[] show;
    private double[] q;

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Fields", "ATT.Q0 ATT.Q1 ATT.Q2 ATT.Q3");
        params.put("Show", "RPY");
        params.put("Scale", 1.0);
        return params;
    }

    @Override
    public void init() {
        super.init();
        q = new double[4];
        param_Fields = ((String) parameters.get("Fields")).split(WHITESPACE_RE);
        param_Scale = (Double) parameters.get("Scale");
        String showStr = ((String) parameters.get("Show")).toUpperCase();
        show = new boolean[]{false, false, false};
        String[] axes = new String[]{"Roll", "Pitch", "Yaw"};
        for (int i = 0; i < 3; i++) {
            String axisName = axes[i];
            show[i] = showStr.contains(axisName.substring(0, 1));
            if (show[i]) {
                addSeries(axisName);
            }
        }
    }

    @Override
    public void process(double time, Map<String, Object> update) {
        if (param_Fields.length < 4) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            Number v = (Number) update.get(param_Fields[i]);
            if (v == null) {
                return;
            }
            q[i] = v.doubleValue();
        }
        double[] euler = RotationConversion.eulerAnglesByQuaternion(q);
        for (int axis = 0; axis < 3; axis++) {
            addPoint(axis, time, euler[axis] * param_Scale);
        }
    }
}
