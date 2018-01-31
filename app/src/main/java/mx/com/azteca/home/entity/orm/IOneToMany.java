package mx.com.azteca.home.entity.orm;

public @interface IOneToMany {
	
	String[] joinColumn() default {};
	boolean cascade() default false;
	
}
