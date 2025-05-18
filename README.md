#بازآرایی

تکنیک `Self Encapsulated Field`:

در این تکنیک، برای استفاده از فیلد های `private` درون خود کلاس ها نیز از متد های `getter` و `setter` استفاده می کنیم. بازآرایی انجام شده در کلاس `Memory` می باشد و برای فیلد های `lastTempIndex` و `lastDataAddress` انجام شده است.

تکنیک `Separate Query From Modifier`:

در این تکنیک تلاش می کنیم متد هایی را شناسایی کنیم که علاوه بر خروجی دادن داده ای را در حافظه تغییر می دهند. برای مثال تابع زیر در کلاس `Memory` هم مقدار `lastTempIndex` را تغییر داده و هم خروجی ای تولید می کند

```
public int getTemp() {
    setLastTempIndex(getLastTempIndex() + tempSize);
    return getLastTempIndex() - tempSize;
}
```

برای بارآرایی کردن این کد باید دو تابع مجزا برای تغییر دادن مقدار `lastTempIndex` و خروجی دادن مقدار آن در نظر گرفته و قبل از استفاده از تابع `getTemp` که از جنس query است، تابع modifier آن را صدا بزنیم:
```
public void modifyLastTempIndex() {
    setLastTempIndex(getLastTempIndex() + tempSize);
}

public int getTemp() {
    return getLastTempIndex() - tempSize;
}
```