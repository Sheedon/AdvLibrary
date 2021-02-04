# AdvLibrary

​		Custom ADV library, support "picture rotation", "video rotation", "web advertising"。



### Gradle

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** Add the dependency

```
dependencies {
	      implementation 'com.github.Sheedon:AdvLibrary:1.0'
}
```



### Maven

**Step 1.** Add the JitPack repository to your build file

```xml
<repositories>
	<repository>
		   <id>jitpack.io</id>
		   <url>https://jitpack.io</url>
	</repository>
</repositories>
```

**Step 2.** Add the dependency

```xml
	<dependency>
	    <groupId>com.github.Sheedon</groupId>
	    <artifactId>AdvLibrary</artifactId>
	    <version>1.0</version>
	</dependency>
```



### Use Library

#### 1. Init Library

```java
AdvFactory.init(Constant.TYPE_ADV_IMAGE);//TYPE_ADV_VIDEO，TYPE_ADV_WEB
```



#### 2.  Download sourceModel

```java
SourceModel sourceModel = new SourceModel();
sourceModel.setImagePaths(new String[]{"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1603365312,3218205429&fm=26&gp=0.jpg"});
AdvFactory.download(sourceModel);
```



#### 3. Configure resource path

##### Xml content

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <org.sheedon.advlibrary.AdvParentView
        android:id="@+id/view_adv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_weight="1"/>


</LinearLayout>
```



##### Bind and configure advertising content

```java
AdvParentView mAdvParent = findViewById(R.id.view_adv);

AdvModel model = new AdvModel();
model.setImagePath("/storage/emulated/0/DCIM");
mAdvParent.initConfig(model);
```



##### Life cycle scheduling

```java
@Override
protected void onResume() {
    super.onResume();
    mAdvParent.onResume();
}

@Override
protected void onPause() {
    super.onPause();
    mAdvParent.onPause();
}

@Override
protected void onDestroy() {
    super.onDestroy();
    mAdvParent.onDestroy();
}
```