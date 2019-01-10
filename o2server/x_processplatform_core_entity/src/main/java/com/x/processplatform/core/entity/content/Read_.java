/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.SliceJpaObject_;
import com.x.processplatform.core.entity.element.ActivityType;
import java.lang.Boolean;
import java.lang.String;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel
(value=com.x.processplatform.core.entity.content.Read.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Thu Dec 27 11:41:12 CST 2018")
public class Read_ extends SliceJpaObject_  {
    public static volatile SingularAttribute<Read,String> activity;
    public static volatile SingularAttribute<Read,String> activityAlias;
    public static volatile SingularAttribute<Read,String> activityDescription;
    public static volatile SingularAttribute<Read,String> activityName;
    public static volatile SingularAttribute<Read,String> activityToken;
    public static volatile SingularAttribute<Read,ActivityType> activityType;
    public static volatile SingularAttribute<Read,String> application;
    public static volatile SingularAttribute<Read,String> applicationAlias;
    public static volatile SingularAttribute<Read,String> applicationName;
    public static volatile SingularAttribute<Read,Boolean> completed;
    public static volatile SingularAttribute<Read,String> creatorIdentity;
    public static volatile SingularAttribute<Read,String> creatorPerson;
    public static volatile SingularAttribute<Read,String> creatorUnit;
    public static volatile SingularAttribute<Read,String> id;
    public static volatile SingularAttribute<Read,String> identity;
    public static volatile SingularAttribute<Read,String> job;
    public static volatile SingularAttribute<Read,String> opinion;
    public static volatile SingularAttribute<Read,String> opinionLob;
    public static volatile SingularAttribute<Read,String> person;
    public static volatile SingularAttribute<Read,String> process;
    public static volatile SingularAttribute<Read,String> processAlias;
    public static volatile SingularAttribute<Read,String> processName;
    public static volatile SingularAttribute<Read,String> serial;
    public static volatile SingularAttribute<Read,Date> startTime;
    public static volatile SingularAttribute<Read,String> startTimeMonth;
    public static volatile SingularAttribute<Read,String> title;
    public static volatile SingularAttribute<Read,String> unit;
    public static volatile SingularAttribute<Read,Boolean> viewed;
    public static volatile SingularAttribute<Read,String> work;
    public static volatile SingularAttribute<Read,String> workCompleted;
}