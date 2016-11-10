# XwsdkPlugin
**This is a news app plugin... (鲜闻插件)**
<br>
<br>
>![image](https://raw.githubusercontent.com/frendyxzc/XwsdkPlugin/master/screenshot/161110.gif)
<br>
<br>

**功能**：

>两行代码几个配置 实现应用内直接安装或打开指定APP(鲜闻)

**原理**：

>封装DroidPlugin，实现鲜闻插件

**用法**：

>1.依赖module - xwsdk;

>2.Application继承XwBaseApplication，如下：
```java
    public class BaseApplication extends XwBaseApplication {
    }
```

>3.AndroidManifest.xml里application添加name：
```java
  <application
      android:name=".BaseApplication"
      ...
  </application>
```

>4.安装或打开鲜闻：
```java
  public class MainActivity extends AppCompatActivity {
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          Button button = (Button) findViewById(R.id.button);
          button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  //安装
                  XwBaseApplication.getXwsdkManager().install();
              }
          });
          Button button1 = (Button) findViewById(R.id.button1);
          button1.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  //打开
                  XwBaseApplication.getXwsdkManager().openXw();
              }
          });
      }
  }
```

<br>
<br>
<br>

Contact Us: 3176385478@qq.com
