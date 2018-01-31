package mx.com.azteca.home.entity.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IOneToOne {

	String[] joinColumn() default {};
	boolean cascade() default false;
	
}
