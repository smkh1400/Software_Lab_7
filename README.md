#بازآرایی

تکنیک `Self Encapsulated Field`:

در این تکنیک، برای استفاده از فیلد های `private` درون خود کلاس ها نیز از متد های `getter` و `setter` استفاده می کنیم. بازآرایی انجام شده در کلاس `Memory` می باشد و برای فیلد های `lastTempIndex` و `lastDataAddress` انجام شده است.

___

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
___

تکنیک `Replace Nested Conditional with Guard Clauses`:
در این تکنیک `if` های تو دو تو را شناسایی می کنیم و در صورتی که بتوانیم با کمک `return` سریع تر از متد خارج شویم از ایجاد `if` تو در تو جلوگیری می کنیم.

در تابع `equals` در کلاس `Token` شرط تو در تو را به صورت زیر مشاهده می کنیم:

```
public boolean equals(Object o) {
    if (o instanceof Token) {
        Token temp = (Token) o;
        if (temp.type == this.type) {
            return this.type != Type.KEYWORDS || this.value.equals(temp.value);
        }
    }
    return false;
}
```

حال با کمک `return` کد را بازآرایی می کنیم و به کد زیر می رسیم:

```
public boolean equals(Object o) {
    if (!(o instanceof Token))
        return false;
    Token temp = (Token) o;
    if (!(temp.type == this.type))
        return false;
    return this.type != Type.KEYWORDS || this.value.equals(temp.value);
}
```

___
تکنیک `Replace Conditional with Polymorphism`:

تابع` semanticFunction(int func, Token next)` موجود در فایل `CodeGenerator.java` شامل یک ساختار `switch-case` با بیش از ۳۰ حالت مختلف می باشد که بر اساس مقدار عددی `func`، عملیات مختلفی مانند `()assign()`، `add()`، `label` و... را فراخوانی می‌کند.


این تکنیک به ما اجازه می‌دهد به جای بررسی عددی مقدار` func`، از آبجکت‌هایی استفاده کنیم که رفتار مورد نظر را در خود پیاده‌سازی کرده‌اند.

مثلا در این تابع، یک قرارداد برای همه‌ی عملیات‌های معنایی ایجاد می کنیم تا هر عملیاتی که قبلاً در یک `case` بود، حالا تبدیل به یک کلاس مستقل شود.

```java
public interface SemanticAction {
    void execute(CodeGenerator codeGenerator, Token token);
}
```

اکنون برای هر عملیات یک کلاس چند ریختی ایجاد می کنیم. مثلا برای عملیات `()add` داریم:

````java

public class AddAction implements SemanticAction {
    @Override
    public void execute(CodeGenerator codeGenerator, Token token) {
        codeGenerator.add();
    }
}

````
سپس یک نگاشت از `func` به کلاس مربوطه تعریف می کنیم:

````java
private final Map<Integer, SemanticAction> actions = new HashMap<>();

public CodeGenerator() {
    symbolTable = new SymbolTable(memory);
    actions.put(9, new AssignAction());
    actions.put(10, new AddAction());
    actions.put(11, new SubAction());
    actions.put(1, (cg, t) -> cg.checkID()); 
    // ... 
}
````

 در انتها متد `semanticFunction` را به شکل زیر بازنویسی می کنیم:

````java
public void semanticFunction(int func, Token next) {
    Log.print("codegenerator : " + func);
    SemanticAction action = actions.get(func);
    if (action != null) {
        action.execute(this, next);
    } else {
        ErrorHandler.printError("Unknown semantic function: " + func);
    }
}
````
این بازآرایی باعث شد کد از حالت خطی خارج شده، وابستگی‌ها کاهش یابد، هر عملیات معنایی به یک واحد مستقل و قابل تست تبدیل شود و در نتیجه توسعه‌پذیری کد به‌طور قابل‌توجهی افزایش یابد.

---
#پاسخ سوالات

سوال اول:
- کد تمیز(clean code): به کدی گفته می شود که در درجه اول قابل نگه داری باشد و همچنین خواندن آن برای دیگران ساده و در کل قابل فهم باشد.
- بدهی فنی(technical debt):  به انتخاب راه‌حل‌های سریع و موقتی در توسعه نرم‌افزار گفته می‌شود که باعث می‌شود در آینده زمان و هزینه‌ی بیشتری صرف نگهداری یا اصلاح کد شود.
- بوی بد (bad smell): نشانه ها و مشکلاتی در کد که نیاز به بارآرایی دارند.


سوال دوم:

- bloaters: این دسته از بو های بد به متد ها و کلاس هایی اشاره دارد که بسیار از حالت عادی بزرگ تر هستند و کار با این کد ها دشوار می باشد. این دسته از مشکلات به مرور زمان به وجود می آیند. 
- object-oriented abusers: این دسته از بو های بد به کد هایی اشاره دارند که از مفاهیم شی گرا به صورت نادرست استفاده می کنند و اصول شی گرا را رعایت نمی کنند.
- change preventers: به کد هایی اشاره دارد که باعث می شوند تغییر در کد مشکل شود. حال این رفتار ممکن است به خاطر وابستگی نادرست بخش های مختلف کد به هم باشد یا نتیجه طراحی نادرست باشد.
- dispensables: کد هایی می باشد که در صورت حذف شدن آن مشکلی در منظق برنامه ایجاد نمی شود و به خوانا تر شدن کد کمک می کند.
- couplers: به کد هایی اشاره دارد که بخش های مختلف آن وابستگی زیادی به هم دارد و این وابستگی، نگهداری کد را دشوار می کند.

