package com.mei.wood.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;

import com.mei.init.BaseApp;
import com.mei.orc.callback.Callback;
import com.mei.orc.http.exception.RxThrowable;
import com.mei.orc.http.listener.RequestListener;
import com.mei.orc.john.model.Empty_data;
import com.mei.orc.util.permission.PermissionCheck;
import com.mei.wood.BuildConfig;
import com.net.network.live.Reservation_Broadcast_Request;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by mylovehang on 2018/5/9.
 */

public class CalendarUtil {
    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "appointment";
    private static String CALENDARS_ACCOUNT_NAME = "appointment@gmail.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.android.appointment";
    private static String CALENDARS_DISPLAY_NAME = "appointment账户";

//    private static long INTERVAL_TIME = 1000 * 60 * 60;

    //检查是否有现有存在的账户。存在则返回账户id，否则返回-1
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null);
        try {
            if (userCursor == null)//查询返回空值
                return -1;
            int count = userCursor.getCount();
            if (count > 0) {//存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    //添加账户。账户创建成功则返回账户id，否则返回-1
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();
        long id = -1;
        try {
            Uri result = context.getContentResolver().insert(calendarUri, value);
            id = result == null ? -1 : ContentUris.parseId(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    //检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    //添加直播到日历，并上报
    public static void addCalendarAndReservation(Context context, String title, String description, int liveId, int userId,
                                                 long beginTime, long endTime) {
        addCalendarAndReservation(context, title, description, liveId, userId, beginTime, endTime, null);
    }

    //添加直播到日历，并上报
    public static void addCalendarAndReservation(Context context, String title, String description, int liveId, int userId,
                                                 long beginTime, long endTime, Callback<Boolean> callback) {
        //上报预约，不需要回调
        BaseApp.Companion.getInstance().getSpiceHolder().getApiSpiceMgr().execute(new Reservation_Broadcast_Request(liveId, 1),
                new RequestListener<Empty_data.Response>() {
                    @Override
                    public void onRequestSuccess(Empty_data.Response response) {
                        if (callback != null)
                            callback.onCallback(true);
                    }

                    @Override
                    public void onRequestFailure(RxThrowable retrofitThrowable) {
                        if (callback != null)
                            callback.onCallback(false);
                    }
                });
        //日历添加事件
        CalendarUtil.addCalendarEvent(context, title, description, liveId, userId, beginTime, endTime);
    }

    //添加事件
    @SuppressLint("MissingPermission")
    public static void addCalendarEvent(Context context, String title, String description, int liveId, int userId, long beginTime, long endTime) {
        // 获取日历账户的id
        int calId = checkAndAddCalendarAccount(context);
        if (calId < 0) {
            // 获取账户id失败直接返回，添加日历事件失败
            return;
        }

        if (isHasAppointment(context, liveId)) return;
        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        // 插入账户的id
        event.put("calendar_id", calId);
        //插入直播id
        event.put(CalendarContract.Events._ID, liveId);
        //插入用户id
        event.put(CalendarContract.Events.ORIGINAL_SYNC_ID, userId);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(beginTime);//设置开始时间
        long start = mCalendar.getTime().getTime();

        mCalendar.setTimeInMillis(endTime);//设置终止时间
        long end = mCalendar.getTime().getTime();

        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");  //这个是时区，必须有，
        event.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, context.getPackageName());
        event.put(CalendarContract.Events.CUSTOM_APP_URI, BuildConfig.APP_SCHEME + "://chosen_featured_tab/0");
        //添加事件
        Uri newEvent = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);
        if (newEvent == null) {
            // 添加日历事件失败直接返回
            return;
        }
        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        // 提前10分钟有提醒
        values.put(CalendarContract.Reminders.MINUTES, 10);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALENDER_REMINDER_URL), values);
        if (uri == null) {
            // 添加闹钟提醒失败直接返回
            return;
        }
    }


    //根据设置的title来查找并删除
    public static void deleteCalendarEvent(Context context, int liveId) {
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null)//查询返回空值
                return;
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    int live_id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Events._ID));
                    if (live_id == liveId) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) {
                            //事件删除失败
                            return;
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    //查询所有直播预约事件
    public static List<String> queryLiveEvent(Context context) {
        List<String> liveIdList = new ArrayList<>();
        if (PermissionCheck.hasPermission(context, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)) {
            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
            try {
                if (eventCursor == null) return null;
                if (eventCursor.getCount() > 0) {
                    for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                        int live_id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Events._ID));
                        if (live_id > 0)
                            liveIdList.add(String.valueOf(live_id));
                    }
                }
            } catch (Exception e) {
                return null;
            } finally {
                if (eventCursor != null) {
                    eventCursor.close();
                }
            }
        }
        return liveIdList;
    }

    //判断直播是否已经预约
    public static boolean isHasAppointment(Context context, int liveId) {
        List<String> idStrs = queryLiveEvent(context);
        if (idStrs != null) {
            return idStrs.contains(String.valueOf(liveId));
        } else {
            return false;
        }
    }

    //通过直播id获取预约的用户id
    public static int getUserId(Context context, int liveId) {
        if (PermissionCheck.hasPermission(context, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)) {
            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
            try {
                if (eventCursor == null)//查询返回空值
                    return 0;
                if (eventCursor.getCount() > 0) {
                    //遍历所有事件，找到title跟需要查询的title一样的项
                    for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                        int live_id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Events._ID));
                        if (live_id == liveId) {
                            return eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Events.ORIGINAL_SYNC_ID));//取得id
                        }
                    }
                }
            } finally {
                if (eventCursor != null) {
                    eventCursor.close();
                }
            }
        }
        return 0;
    }

    //获取直播开始时间
    public static long getBeginTime(Context context, int liveId) {
        if (PermissionCheck.hasPermission(context, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)) {
            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
            try {
                if (eventCursor == null)//查询返回空值
                    return 0;
                if (eventCursor.getCount() > 0) {
                    //遍历所有事件，找到title跟需要查询的title一样的项
                    for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                        int live_id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Events._ID));
                        if (live_id == liveId) {
                            return eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Events.DTSTART));
                        }
                    }
                }
            } finally {
                if (eventCursor != null) {
                    eventCursor.close();
                }
            }
        }
        return 0;
    }
}
