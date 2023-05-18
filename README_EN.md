English | [简体中文](README.md)

# **HTEffect SDK**

<br/>

## **Instruction**

- Exquisite facial beauty and hair color transformation effects
- More than 60 types of beauty filters, wonderful effect filters, and interesting distorting mirror with deformed faces
- Exquisite 2D dynamic stickers, 3D props, masks, and rich full screen 3D gift effects
- Draggable watermark stickers: supports users to upload, drag, zoom, rotate, etc
- Multiple gesture recognition and trigger special effects
- Cutout function supports AI background segmentation and green screen segmentation with multiple screen colors

<br/>

### **Feature**

- [More diverse] Multiple AR special effects are fully covered, with a variety of special effects types to meet the diverse needs of users
- [More convenient docking] Out of the box UI can be directly used on the C end, with fast docking and a good experience. Three lines of code can quickly achieve docking
- [Resource support self design] All materials support platform self-design and customization, creating AR material special effects that align with platform positioning
- [Ultimate performance] Industry leading AI algorithm capability, 946 facial key points make facial features more precise, and facial expression and emotion capture more efficient, stable, and accurate
- [More independent platform based services] Users can register themselves and log in to Hongtu AI open platform to obtain the portrait body SDK capability, and have more free and accurate control over application status and information in real time

<br/>

### **Results**

- By utilizing AR effects such as facial beauty, stickers, props, gesture effects, and portrait background segmentation, we provide users with rich and diverse AI+AR technologies that revolve around the human body

<center>
	<img src="./documents/imgs/sticker.png" width="20%" />
    <img src="./documents/imgs/matting.png" width="20%" />
    <img src="./documents/imgs/filter.png" width="20%" />
    <img src="./documents/imgs/beauty.png" width="20%" />
</center>
<br/>
<br/>

---

## **Quick start**

### **iOS**

#### **1. Prerequisites**

- Xcode version 13.0 or later
- iOS version 11.0 or later
- APP ID, acquired from [HONGTU AI open platform](https://console.texeljoy.com/login) console

#### **2. Installation**

You can use CocoaPods to automatically load, or download the SDK and then import it into your project

**CocoaPods**

- Open **Podfile**, and add the **HTEffect** pod.

```shell
pod 'HTEffect'
```

- Install

```shell
pod install
```

**Manual**

- Place the downloaded **HTEffect.framework** library file and **HTEffect.bundle** resource package in your project folder
- Add a dynamic library in Xcode > General, ensuring that the **Embed** property is set to **Embed&Sign**
- Search for bitcode in Xcode > Build Settings and set **Enable Bitcode** to **NO**
- Add **App Transport Security Settings** > **Allow Arbitrary Loads** in Xcode>Info and set it to **YES**

#### **3. Reference**

- Add module references to the files that require the use of SDK APIs in the project

```objective-c
#import <HTEffect/HTEffectInterface.h>
```

- (Optional) HTUI can be selected according to project requirements. Add the HTUI folder to your project folder and add references to the files that need to be used in the project

```objective-c
#import "HTUIManager.h"
```

#### **4. Usage**

**Initialization**

- Before calling the relevant functions of HTEffect on your app (it is recommended to set the following settings in [AppDelegate application: didFinishLaunchingWithOptions:])

```objective-c
/**
* Online authentication method
*/
[[HTEffect shareInstance] initHTEffect:@"YOUR_APP_ID" withDelegate:self];

/**
* Offline authentication method
*/
// [[HTEffect shareInstance] initHTEffect:@"YOUR_APP_ID"];
```

- (Optional) If you need to use HTUI, you can add the following method in viewDidLoad

```objective-c
[[HTUIManager shareManager] loadToWindowDelegate:self];
[self.view addSubview:[HTUIManager shareManager].defaultButton];
```

**Rendering**

- Define a BOOL variable **isRenderInit** to indicate the initialization status of the renderer, and use the corresponding method to render based on the obtained video format

```objective-c
/**
* Video frames
*/
CVPixelBufferLockBaseAddress(pixelBuffer, 0);
unsigned char *buffer = (unsigned char *) CVPixelBufferGetBaseAddressOfPlane(pixelBuffer, 0);

if (!_isRenderInit) {
    [[HTEffect shareInstance] releaseBufferRenderer];
    _isRenderInit = [[HTEffect shareInstance] initBufferRenderer:format width:width height:height rotation:rotation isMirror:isMirror maxFaces:maxFaces];
}
[[HTEffect shareInstance] processBuffer:buffer];

CVPixelBufferUnlockBaseAddress(pixelBuffer, 0);

/**
* Texture
*/
// if (!_isRenderInit) {
//     [[HTEffect shareInstance] releaseTextureRenderer];
//     _isRenderInit = [[HTEffect shareInstance] initTextureRenderer:width height:height rotation:rotation isMirror:isMirror maxFaces:maxFaces];
// }
// [[HTEffect shareInstance] processTexture:textureId];
```

**Destruction**

- At the end of rendering, the corresponding release method needs to be called based on the video format, usually written in the dealloc method

```objective-c
/**
* Destroy texture rendering resources
*/
[[HTEffect shareInstance] releaseTextureRenderer];

/**
* Destroy buffer rendering resources
*/
// [[HTEffect shareInstance] releaseBufferRenderer];
```

<br/>

### **Android**

#### **1. Import project**

- Copy the **HTEffect.aar** file to the libs folder in the app module, and add the following dependencies in the dependencies of the build.gradle file in the app module

```shell
dependencies {
 implementation files('libs/HTEffect.aar')
}
```

- Copy the **libHTEffect.so** files corresponding to each ABI in the jniLibs folder to the corresponding directory
- Copy the assets resource file to the corresponding directory of the project

#### **2. Use HTUI (optional)**

- Relying on our HTUI project and using the open-source UI library we provide, copy the HTUI folder to the root directory of the project. In the settings.gradle file of the project root directory, add the following code

```java
include(":htui")
```

- In the dependencies section of the build.gradle file in the app module, add the following code

```shell
implementation project(':tiui')
```

#### **3. Integration**

**Initialization**

- The HTEffect initialization function takes effect after being called once in the program. It is recommended that you call it when the Application is created; If the rendering function is not frequently used, it can also be called during use, with the following interfaces

```java
// Online authentication method
HTEffect.shareInstance().initHTEffect(context, "YOUR_APP_ID", new InitCallback() {
        @Override public void onInitSuccess() {}
        @Override public void onInitFailure() {}
});
// Offline authentication method
//HTEffect.shareInstance().initHTEffect(context,"YOUR_APP_ID");
```

**Add HTUI (optional)**

- Set whether to inherit or indirectly inherit FragmentActivity using htui's activity, such as

```java
public class CameraActivity extends FragmentActivity {
    //...
}
```

- If you need to use htui, please call addcontentView to add the UI. The code is as follows

```java
addContentView(
    new HTPanelLayout(this).init(getSupportFragmentManager()),
    new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT)
);
```

**Rendering**

- Define the Boolean variable **isRenderInit** to indicate whether the rendering method has been initialized, and then use the corresponding method for rendering based on the different video frame formats obtained

```java
/**
 * texture format GL_TEXTURE_EXTERNAL_OES
*/
if (!isRenderInit) {
    isRenderInit = HTEffect.shareInstance().initTextureOESRenderer(width, height, rotation, isMirror, maxFaces);
}
int textureId = HTEffect.shareInstance().processTextureOES(textureOES);

/**
 * texture format GL_TEXTURE_2D
*/
if (!isRenderInit) {
    isRenderInit = HTEffect.shareInstance().initTextureRenderer(width, height, rotation, isMirror, maxFaces);
}
int textureId = HTEffect.shareInstance().processTexture(texture2D);

/**
 * Video frames
*/
if (!isRenderInit) {
    isRenderInit = HTEffect.shareInstance().initBufferRenderer(format,width, height, rotation, isMirror, maxFaces);
}
HTEffect.shareInstance().processBuffer(buffer);
```

**Destruction**

- At the end of rendering, in order to prevent memory leak, it is necessary to call the corresponding destroy method to release resources according to the different video frame formats. The call location is usually at the destruction of the video frame callback interface, or at the end of the life cycle of Activity and Fragment. At the same time, set the defined boolean variable **isRenderInit** to false

```java
/**
 * Use one of them
 */
HTEffect.shareInstance().releaseTextureOESRenderer();
HTEffect.shareInstance().releaseTextureRenderer();
HTEffect.shareInstance().releaseBufferRenderer();

/*
* Set isRenderInit to false
*/
isRenderInit = false;
```

<br/>

---

## **Demo**

> [Link](https://doc.texeljoy.com/hummanBody/beauty/SDK/sdkDownload.html)

<br/>

---

## **Recent updates**

- **2023.05.17:** New 2.0 version

  - More than 30 new styles of filters, special effect filters, and distorting mirror
  - Add hairdressing, masks, gifts, and custom drag and drop watermarks
  - Green screen cutout adds blue and white screen colors and parameter adjustments
  - Optimized gesture effects and portrait segmentation effects
- [More](https://doc.texeljoy.com/hummanBody/beauty/API/api.html)

<br/>

---

## **Communication & Feedback**

### 1. Official website: [www.texeljoy.com](https://www.texeljoy.com)

### 2. Cooperation number: 400-178-9918

### 3. E-mail address: business@texeljoy.com

### 4. Official account:

<div align="left">
<img src="./documents/imgs/public.png"  width = "200" height = "200" />
</div>
