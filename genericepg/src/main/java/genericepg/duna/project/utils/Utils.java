package genericepg.duna.project.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import genericepg.duna.project.R;
import genericepg.duna.project.model.BaseProgramModel;
import genericepg.duna.project.model.BaseTimelineModel;


/**
 * Created by Marius Duna on 9/15/2016.
 */
public class Utils {
    private static BaseProgramModel programModel = new BaseProgramModel();
    private static BaseTimelineModel timelineModel = new BaseTimelineModel();
    private static double pxPerMinConstant(Context context) {
        return convertDpToPixel(context.getResources().getDimension(R.dimen.epg_width_one_min), context);
    }

    public static double convertDpToPixel(double dp, Context context) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)dp, r.getDisplayMetrics());
    }

    public static double convertMillisecondsToPx(double milliseconds, Context context) {
        return milliseconds * pxPerMinConstant(context) / TimeUnit.MINUTES.toMillis(1);
    }

    public static double convertPxToMilliseconds(double px, Context context) {
        return TimeUnit.MINUTES.toMillis(1) * px / pxPerMinConstant(context);
    }

    public static int getInitialPositionInList(double currentTime, ArrayList<BaseProgramModel> arrayList) {
        programModel.setStartTime((long) currentTime);
        int pos = Collections.binarySearch(arrayList, programModel, comparatorProgram);
        //TODO handle the situation when the list has less than 2 items
        if (pos < 0) pos = Math.abs(pos) - 2;
        return pos;
    }

    private static Comparator<BaseProgramModel> comparatorProgram = new Comparator<BaseProgramModel>() {
        public int compare(BaseProgramModel u1, BaseProgramModel u2) {
            return (int) (u1.getStartTime() - u2.getStartTime());
        }
    };

    public static int getInitialPositionInTimelineList(double currentTime, ArrayList<BaseTimelineModel> arrayList) {
        timelineModel.setTime((long) currentTime);
        int pos = Collections.binarySearch(arrayList, timelineModel, comparatorTime);
        //TODO handle the situation when the list has less than 2 items
        if (pos < 0) pos = Math.abs(pos) - 2;
        return pos;
    }

    private static Comparator<BaseTimelineModel> comparatorTime = new Comparator<BaseTimelineModel>() {
        public int compare(BaseTimelineModel u1, BaseTimelineModel u2) {
            return (int) (u1.getTime() - u2.getTime());
        }
    };

    public static int getInitialProgramOffsetPx(double programStartTime, double systemTime, Context context) {
        double offsetTime = systemTime - programStartTime;
        return (int) convertMillisecondsToPx(offsetTime, context);
    }
}
