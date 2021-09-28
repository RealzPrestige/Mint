package mint.modules;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ModuleRegistry {
    String name() default "";
    String description() default "";
    Module.Category category();

}
