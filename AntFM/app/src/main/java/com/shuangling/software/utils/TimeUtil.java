package com.shuangling.software.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author pangzf
 * @desc:时间工具类
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {
    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getChatTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;
            default:
                // result = temp + "天前 ";
                result = getTime(timesamp);
                break;
        }
        return result;
    }
/**
 * 格式化时间
 * @param time
 * @return
 */
//	public static String formatDateTime(String time) {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		if(time==null ||"".equals(time)){
//			return "";
//		}
//		Date date = null;
//		try {
//			date = format.parse(time);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		Calendar current = Calendar.getInstance();
//
//		Calendar today = Calendar.getInstance();	//今天
//
//		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
//		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
//		today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
//		//  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
//		today.set( Calendar.HOUR_OF_DAY, 0);
//		today.set( Calendar.MINUTE, 0);
//		today.set(Calendar.SECOND, 0);
//
//		Calendar yesterday = Calendar.getInstance();	//昨天
//
//		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
//		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
//		yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);
//		yesterday.set( Calendar.HOUR_OF_DAY, 0);
//		yesterday.set( Calendar.MINUTE, 0);
//		yesterday.set(Calendar.SECOND, 0);
//
//		current.setTime(date);
//
//		if(current.after(today)){
//			return ""+time.split(" ")[1];
//		}else if(current.before(today) && current.after(yesterday)){
//
//			return "昨天 "+time.split(" ")[1];
//		}else{
//			int index = time.indexOf("-")+1;
//			return time.substring(index, time.length());
//		}
//	}

    /**
     * 将秒转成分秒
     *
     * @return
     */
    public static String getVoiceRecorderTime(int time) {
        int minute = time / 60;
        int second = time % 60;
        if (minute == 0) {
            return String.valueOf(second);
        }
        return minute + ":" + second;
    }

    /**
     * 计算相差的小时
     *
     * @param starTime
     * @param endTime
     * @return
     */
    public static int getTimeDifferenceHour(String starTime, String endTime) {
        int timeString = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);
            long diff = parse1.getTime() - parse.getTime();
            String string = Long.toString(diff);
            float parseFloat = Float.parseFloat(string);
            float hour1 = parseFloat / (1000);
            timeString = (int) hour1;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;
    }

    /**
     * 判断时间差 1 min；小于1min 返回null，
     *
     * @param time1
     * @param before1
     * @return
     */
    public static String getTime(String time1, String before1) {
        String show_time = null;
        if (before1 != null) {
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = df.parse(time1);
                Date date = df.parse(before1);
                long l = now.getTime() - date.getTime();
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                if (min >= 1) {
                    show_time = time1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                show_time = time1;
            }
        } else {
            show_time = time1;
        }
        return show_time;
    }

    /**
     * 计算相差的小时
     *
     * @param
     * @param endTime
     * @return
     */
    public static String timetrans(String endTime) {
//		if (!checkDate(endTime)) {
//			return endTime;
//		}
        String timeString = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse1 = df.parse(endTime);
            timeString = dateFormat.format(parse1);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            timeString = endTime;
        }
        return timeString;
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    // 检查日期格式 YYYY-MM-DD
    public static boolean checkDate(String sourceDate) {
        if (sourceDate == null || sourceDate.trim().length() == 0) {
            return false;
        }
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(sourceDate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String formatDateTime(String time_at) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = "";
        try {
            Date timeAt = dateFormat.parse(time_at);
            //Date today = new Date();
            Calendar today = Calendar.getInstance();    //今天
            long difference = today.getTime().getTime() - timeAt.getTime();
            long day = difference / (3600 * 24 * 1000);
            long hour = (difference / (3600 * 1000));
            long minute = (difference / (60 * 1000));
            long second = difference / 1000;
            Calendar current = Calendar.getInstance();
            int currentYear = current.get(Calendar.YEAR);
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            Calendar yesterday = Calendar.getInstance();    //昨天
            yesterday.set(Calendar.YEAR, today.get(Calendar.YEAR));
            yesterday.set(Calendar.MONTH, today.get(Calendar.MONTH));
            yesterday.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) - 1);
            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);
            Calendar dateBeforeYesterday = Calendar.getInstance();    //昨天
            dateBeforeYesterday.set(Calendar.YEAR, today.get(Calendar.YEAR));
            dateBeforeYesterday.set(Calendar.MONTH, today.get(Calendar.MONTH));
            dateBeforeYesterday.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) - 2);
            dateBeforeYesterday.set(Calendar.HOUR_OF_DAY, 0);
            dateBeforeYesterday.set(Calendar.MINUTE, 0);
            dateBeforeYesterday.set(Calendar.SECOND, 0);
            current.setTime(timeAt);
            int timeYear = current.get(Calendar.YEAR);
            if (minute < 1) {
                time = "刚刚";
            } else if (hour < 1) {
                time = minute + "分钟前";
            } else if (current.after(today)) {
                time = "" + hour + "小时前";
            } else if (current.before(today) && current.after(yesterday)) {
                time = "昨天" + time_at.split(" ")[1].substring(0, 5);
            } else if (current.before(yesterday) && current.after(dateBeforeYesterday)) {
                time = "前天" + time_at.split(" ")[1].substring(0, 5);
            } else {
                String[] strarr = time_at.split(" ")[0].split("-");
                if (timeYear != currentYear) {
                    time = strarr[0] + "年" + strarr[1] + "月" + strarr[2] + "日";
                } else {
                    time = strarr[1] + "月" + strarr[2] + "日";
                }
            }
        } catch (Exception e) {
        }
        return time;
    }



    public static String formatDateTime01(String time_at) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = "";
        try {
            Date timeAt = dateFormat.parse(time_at);
            //Date today = new Date();
            Calendar today = Calendar.getInstance();    //今天
            long difference = today.getTime().getTime() - timeAt.getTime();
            long day = difference / (3600 * 24 * 1000);
            long hour = (difference / (3600 * 1000));
            long minute = (difference / (60 * 1000));
            long second = difference / 1000;
            Calendar current = Calendar.getInstance();
            int currentYear = current.get(Calendar.YEAR);
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            Calendar yesterday = Calendar.getInstance();    //昨天
            yesterday.set(Calendar.YEAR, today.get(Calendar.YEAR));
            yesterday.set(Calendar.MONTH, today.get(Calendar.MONTH));
            yesterday.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) - 1);
            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);
            Calendar dateBeforeYesterday = Calendar.getInstance();    //昨天
            dateBeforeYesterday.set(Calendar.YEAR, today.get(Calendar.YEAR));
            dateBeforeYesterday.set(Calendar.MONTH, today.get(Calendar.MONTH));
            dateBeforeYesterday.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) - 2);
            dateBeforeYesterday.set(Calendar.HOUR_OF_DAY, 0);
            dateBeforeYesterday.set(Calendar.MINUTE, 0);
            dateBeforeYesterday.set(Calendar.SECOND, 0);
            current.setTime(timeAt);
            int timeYear = current.get(Calendar.YEAR);
            if (minute < 1) {
                time = "刚刚";
            } else if (hour < 1) {
                time = minute + "分钟前";
            } else if (current.after(today)) {
                time = "" + hour + "小时前";
            } else if (current.before(today) && current.after(yesterday)) {
                time = "昨天" + time_at.split(" ")[1].substring(0, 5);
            } else if (current.before(yesterday) && current.after(dateBeforeYesterday)) {
                time = "前天" + time_at.split(" ")[1].substring(0, 5);
            } else {
                String[] strarr = time_at.split(" ")[0].split("-");
                if (timeYear != currentYear) {
                    time = strarr[0] + "年" + strarr[1] + "月" + strarr[2] + "日";
                } else {
                    time = strarr[1] + "月" + strarr[2] + "日";
                }
            }
        } catch (Exception e) {
        }
        return time;
    }

    public static String getTimeSpan(String starTime, String endTime) {
        int timeString = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date start = dateFormat.parse(starTime);
            Date end = dateFormat.parse(endTime);
            long milliseconds = end.getTime() - start.getTime();
            long seconds = milliseconds / 1000;
            String timecodeSeconds = "" + (seconds % 60);
            String timecodeMinutes = "" + (seconds / 60) % 60;
            String timecodeHours = "" + (seconds / 3600);
            if ((seconds % 60) < 10) {
                timecodeSeconds = "0" + timecodeSeconds;
            }
            if ((seconds / 60) % 60 < 10) {
                timecodeMinutes = "0" + timecodeMinutes;
            }
            if (seconds / 3600 < 10) {
                timecodeHours = "0" + timecodeHours;
            }
            if (seconds / 60 < 1) {
                return timecodeSeconds + "秒";
            } else if (seconds / 3600 < 1) {
                return timecodeMinutes + "分" + timecodeSeconds + "秒";
            } else {
                return timecodeHours + "时" + timecodeMinutes + "分" + timecodeSeconds + "秒";
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
}
