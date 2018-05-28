
package android.databinding;
import jeeryweb.geocast.BR;
class DataBinderMapper  {
    final static int TARGET_MIN_SDK = 21;
    public DataBinderMapper() {
    }
    public android.databinding.ViewDataBinding getDataBinder(android.databinding.DataBindingComponent bindingComponent, android.view.View view, int layoutId) {
        switch(layoutId) {
                case jeeryweb.geocast.R.layout.reliabilities_row_record:
                    return jeeryweb.geocast.databinding.ReliabilitiesRowRecordBinding.bind(view, bindingComponent);
                case jeeryweb.geocast.R.layout.sent_row_record:
                    return jeeryweb.geocast.databinding.SentRowRecordBinding.bind(view, bindingComponent);
                case jeeryweb.geocast.R.layout.activity_sent:
                    return jeeryweb.geocast.databinding.ActivitySentBinding.bind(view, bindingComponent);
                case jeeryweb.geocast.R.layout.inbox_row_record:
                    return jeeryweb.geocast.databinding.InboxRowRecordBinding.bind(view, bindingComponent);
                case jeeryweb.geocast.R.layout.activity_inbox:
                    return jeeryweb.geocast.databinding.ActivityInboxBinding.bind(view, bindingComponent);
        }
        return null;
    }
    android.databinding.ViewDataBinding getDataBinder(android.databinding.DataBindingComponent bindingComponent, android.view.View[] views, int layoutId) {
        switch(layoutId) {
        }
        return null;
    }
    int getLayoutId(String tag) {
        if (tag == null) {
            return 0;
        }
        final int code = tag.hashCode();
        switch(code) {
            case 742237459: {
                if(tag.equals("layout/reliabilities_row_record_0")) {
                    return jeeryweb.geocast.R.layout.reliabilities_row_record;
                }
                break;
            }
            case -1336209879: {
                if(tag.equals("layout/sent_row_record_0")) {
                    return jeeryweb.geocast.R.layout.sent_row_record;
                }
                break;
            }
            case 599376788: {
                if(tag.equals("layout/activity_sent_0")) {
                    return jeeryweb.geocast.R.layout.activity_sent;
                }
                break;
            }
            case 2071455909: {
                if(tag.equals("layout/inbox_row_record_0")) {
                    return jeeryweb.geocast.R.layout.inbox_row_record;
                }
                break;
            }
            case 1362165708: {
                if(tag.equals("layout/activity_inbox_0")) {
                    return jeeryweb.geocast.R.layout.activity_inbox;
                }
                break;
            }
        }
        return 0;
    }
    String convertBrIdToString(int id) {
        if (id < 0 || id >= InnerBrLookup.sKeys.length) {
            return null;
        }
        return InnerBrLookup.sKeys[id];
    }
    private static class InnerBrLookup {
        static String[] sKeys = new String[]{
            "_all"};
    }
}