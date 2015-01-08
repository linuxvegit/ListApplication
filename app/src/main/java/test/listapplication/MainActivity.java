package test.listapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by MouXK on 15/1/6.
 */
public class MainActivity extends Activity {
    private RefreshableView refreshableView;
    private ListView listView;

    private List<Map<String, Object>> list = new ArrayList<>();//好友状态列表
    private Map<String, Object> titleMap = new HashMap<>();//标题内容映射

    private final Integer[] icons = new Integer[]{R.drawable.icon3, R.drawable.icon2, R.drawable.icon1, R.drawable.icon4, R.drawable.icon5};
    private final Integer[] userNames = new Integer[]{R.string.userName1, R.string.userName2, R.string.userName3, R.string.userName4, R.string.userName5};
    private final Integer[] details = new Integer[]{R.string.shareALink, R.string.empty, R.string.empty, R.string.shareALink, R.string.empty};
    private final Integer[] contents = new Integer[]{R.string.content1, R.string.content2, R.string.content3, R.string.content4, R.string.content5};


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainactivity);

        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, 0);

        listView = (ListView) findViewById(R.id.list_view);

        setTitle();
        setList();
        setAdapter();
    }

    /**
     * 设置标题内容
     * 存储在titleMap中，
     * key：
     * icon：头像
     * userName：用户名
     * background：背景
     */
    private void setTitle() {
        titleMap.put("icon", R.drawable.icon);
        titleMap.put("userName", R.string.userName);
        titleMap.put("background", R.drawable.back2);
    }

    /**
     * 设置好友状态内容
     * 存储在list中
     * 每一项对应一个映射，对应一个好友状态
     * key：
     * icon：好友头像
     * userName：好友用户名
     * detail：状态附属属性，例如:分享了一个链接
     * content：状态具体内容
     * isLike：用户是否已经赞过该条状态
     */
    private void setList() {
        for (int i = 0; i < 5; ++i) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", icons[i]);
            map.put("userName", userNames[i]);
            map.put("detail", details[i]);
            map.put("content", contents[i]);
            map.put("isLike", false);
            list.add(map);
        }
    }

    /**
     *
     */
    private void setAdapter() {
        MyAdapter adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
    }

    /**
     * 标题容器类
     */
    public final class TitleHolder {
        public ImageView backgroundImg;//背景
        public ImageView iconImg;//用户头像
        public TextView userName;//用户名
    }

    /**
     * 好友状态容器类
     */
    public final class MsgHolder {
        public ImageView iconImg;//好友头像
        public TextView userName;//好友用户名
        public TextView detailMsg;//状态附属属性
        public TextView content;//状态具体内容
        public TextView updateTime;//状态发送时间
        public ImageButton goodButton;//点赞按钮
    }

    public class MyAdapter extends BaseAdapter {
        private boolean isTitleHolder = false;

        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return list.size() + 1;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            //标题
            if (position == 0) {
                TitleHolder holder = null;

                if (convertView == null || !isTitleHolder) {
                    holder = new TitleHolder();
                    //初始化标题元素
                    convertView = inflater.inflate(R.layout.title, null);
                    holder.backgroundImg = (ImageView) convertView.findViewById(R.id.titleBackground);
                    holder.iconImg = (ImageView) convertView.findViewById(R.id.titleIcon);
                    holder.userName = (TextView) convertView.findViewById(R.id.titleUserName);
                    convertView.setTag(holder);
                } else {
                    holder = (TitleHolder) convertView.getTag();
                }
                isTitleHolder = true;
                //设置用户名
                holder.userName.setText(getResources().getString((Integer) titleMap.get("userName")));
                //设置用户头像
                holder.iconImg.setBackgroundResource((Integer) titleMap.get("icon"));
                holder.iconImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                        startActivity(intent);
                    }
                });
                //设置背景图片
                holder.backgroundImg.setBackgroundResource((Integer) titleMap.get("background"));
                return convertView;
            }
            //状态
            else {
                MsgHolder holder = null;

                if (convertView == null || isTitleHolder) {
                    holder = new MsgHolder();
                    convertView = inflater.inflate(R.layout.msg, null);
                    //初始化状态元素
                    holder.iconImg = (ImageView) convertView.findViewById(R.id.msgIcon);
                    holder.userName = (TextView) convertView.findViewById(R.id.msgUserName);
                    holder.detailMsg = (TextView) convertView.findViewById(R.id.msgDetail);
                    holder.content = (TextView) convertView.findViewById(R.id.msgContent);
                    holder.updateTime = (TextView) convertView.findViewById(R.id.msgTime);
                    holder.goodButton = (ImageButton) convertView.findViewById(R.id.msgGoodButton);
                    convertView.setTag(holder);
                } else {
                    holder = (MsgHolder) convertView.getTag();
                }
                isTitleHolder = false;
                //设置好友头像
                holder.iconImg.setBackgroundResource((Integer) list.get(position - 1).get("icon"));
                holder.iconImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                        startActivity(intent);
                    }
                });
                //设置用户
                holder.userName.setText(getResources().getString((Integer) list.get(position - 1).get("userName")));
                holder.userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                //设置附属信息
                holder.detailMsg.setText(getResources().getString((Integer) list.get(position - 1).get("detail")));
                //设置状态具体内容
                holder.content.setText(getResources().getString((Integer) list.get(position - 1).get("content")));
                //点赞按钮逻辑
                final MsgHolder tempHolder = holder;
                final List<Map<String, Object>> tempList = list;
                if ((Boolean) list.get(position - 1).get("isLike"))
                    holder.goodButton.setBackgroundResource(R.drawable.fullheartbutton);
                else holder.goodButton.setBackgroundResource(R.drawable.emptyheartbutton);
                holder.goodButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!(Boolean) tempList.get(position - 1).get("isLike")) {
                            tempHolder.goodButton.setBackgroundResource(R.drawable.fullheartbutton);
                            tempList.get(position - 1).put("isLike", true);
                        }
                    }
                });
                return convertView;
            }
        }
    }
}
