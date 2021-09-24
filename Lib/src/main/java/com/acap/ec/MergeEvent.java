package com.acap.ec;

import com.acap.ec.internal.MergeException;
import com.acap.ec.internal.ProxyEventListener;
import com.acap.ec.internal.Result;
import com.acap.ec.listener.OnEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * Tip:
 *      并发事件<br/>
 *      当前事件包含多个子事件，当所有事件完成时候
 *
 *
 *
 * Created by A·Cap on 2021/9/18 17:28
 * </pre>
 */
@SuppressWarnings("unchecked")
public class MergeEvent<P, R> extends BaseEvent<P, List<R>> {

    private Event<P, R>[] mEvents;

    private final Map<Event<P, R>, Result<P, R>> mEventResult = new ConcurrentHashMap<>();
    private final Map<Event<P, R>, Boolean> mEventComplete = new ConcurrentHashMap<>();

    public MergeEvent(Event<P, ? extends R>... event) {
        if (event != null) {
            mEvents = new Event[event.length];
            for (int i = 0; i < event.length; i++) {
                Event<P, R> e = (Event<P, R>) event[i];
                mEvents[i] = e;
                if (e != null) {
                    e.listener(new ProxyEventListener<>(new MergeEventListener()));
                    mEventComplete.put(e, false);
                }
            }
        }
    }

    @Override
    protected void onFinish(boolean isComplete) {
        if (mEvents != null && mEvents.length > 0) {
            for (Event<P, R> mEvent : mEvents) {
                mEvent.finish(isComplete);
            }
        }
        super.onFinish(isComplete);
    }

    @Override
    protected void onCall(P params) {
        if (mEvents != null && mEvents.length > 0) {
            for (int i = 0; i < mEvents.length; i++) {
                Event<P, R> event = mEvents[i];
                if (event != null) {
                    event.start(params);
                }
            }
        } else {
            next(Utils.getResult(mEvents, mEventResult));
        }
    }

    //检测完成
    private void checkComplete() {
        if (Utils.isComplete(mEvents, mEventComplete)) {
            if (Utils.isError(mEvents, mEventResult)) {
                error(Utils.getError(mEvents, mEventResult));
            } else {
                next(Utils.getResult(mEvents, mEventResult));
            }
        }
    }

    private synchronized void onError(Event<P, R> event, Throwable e) {
        mEventResult.put(event, new Result<>(event, e));
    }

    private synchronized void onNext(Event<P, R> event, R result) {
        mEventResult.put(event, new Result<>(event, result));
    }

    private synchronized void onComplete(Event<P, R> event) {
        mEventComplete.put(event, true);
        checkComplete();
    }


    // 子事件监听
    private class MergeEventListener implements OnEventListener<P, R> {
        private Event<P, R> mEvent;

        @Override
        public void onStart(Event<P, R> event, P params) {
            mEvent = event;
        }

        @Override
        public void onError(Throwable e) {
            MergeEvent.this.onError(mEvent, e);
        }

        @Override
        public void onNext(R result) {
            MergeEvent.this.onNext(mEvent, result);
        }

        @Override
        public void onComplete() {
            MergeEvent.this.onComplete(mEvent);
        }
    }

    private static final class Utils {

        // 判断是否完成
        public static final <P, R> boolean isComplete(Event<P, R>[] events, Map<Event<P, R>, Boolean> map) {
            if (events == null || events.length == 0) {
                return true;
            }

            for (int i = 0; i < events.length; i++) {
                Event event = events[i];
                if (event != null && map.get(event) == false) {
                    return false;
                }
            }

            return true;
        }

        // 是否获得结果
        public static final <P, R> boolean isResult(Event<P, R>[] events, Map<Event<P, R>, Result<P, R>> map) {
            if (events == null || events.length == 0) {
                return true;
            }
            for (int i = 0; i < events.length; i++) {
                Event event = events[i];
                if (event != null && map.get(event) == null) {
                    return false;
                }
            }
            return true;
        }

        // 判断是否错误
        public static final <P, R> boolean isError(Event<P, R>[] events, Map<Event<P, R>, Result<P, R>> map) {
            if (events == null || events.length == 0) {
                return true;
            }
            for (int i = 0; i < events.length; i++) {
                Event event = events[i];
                if (event != null) {
                    Result<P, R> result = map.get(event);
                    if (result != null && result.isError()) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static final <P, R> MergeException getError(Event<P, R>[] events, Map<Event<P, R>, Result<P, R>> map) {
            MergeException err = new MergeException();
            if (events == null || events.length == 0) {
                return err;
            }

            for (int i = 0; i < events.length; i++) {
                Event event = events[i];
                if (event != null) {
                    Result<P, R> result = map.get(event);
                    if (result != null && result.isError()) {
                        err.put(result.getThrowable());
                    }
                }
            }
            return err;
        }

        public static final <P, R> List<R> getResult(Event<P, R>[] events, Map<Event<P, R>, Result<P, R>> map) {
            if (events == null || events.length == 0) {
                return new ArrayList<>();
            }

            List<R> array = new ArrayList<>();

            for (int i = 0; i < events.length; i++) {
                Event event = events[i];
                if (event != null) {
                    Result<P, R> result = map.get(event);
                    if (result != null && !result.isError()) {
                        array.add(result.getResult());
                    } else {
                        array.add(null);
                    }
                } else {
                    array.add(null);
                }
            }
            return array;
        }

        public static final <P, R> void init(Map<Event<P, R>, Result<P, R>> map) {
            if (map != null) {
                map.clear();
            }
        }
    }

}
