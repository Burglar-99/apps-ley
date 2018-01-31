package mx.com.azteca.home.entity.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IColumn {

	String name() default "";
	boolean allowNull() default true;
	String dataType() default "text";
}