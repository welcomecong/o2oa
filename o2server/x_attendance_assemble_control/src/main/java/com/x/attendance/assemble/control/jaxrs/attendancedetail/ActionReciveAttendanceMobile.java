package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.organization.Person;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionReciveAttendanceMobile extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionReciveAttendanceMobile.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Date datetime = null;
		DateOperation dateOperation = new DateOperation();
		EffectivePerson currentPerson = this.effectivePerson( request );
		AttendanceDetailMobile attendanceDetailMobile = new AttendanceDetailMobile();
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}

		if( check ){
			if( StringUtils.isNotEmpty(wrapIn.getRecordAddress()) ){
				attendanceDetailMobile.setRecordAddress( wrapIn.getRecordAddress() );
			}
		}
		if( check ){
			if( StringUtils.isNotEmpty(wrapIn.getLatitude())){
				attendanceDetailMobile.setLatitude( wrapIn.getLatitude() );
			}
		}
		if( check ){
			if( StringUtils.isNotEmpty(wrapIn.getLongitude())){
				attendanceDetailMobile.setLongitude( wrapIn.getLongitude() );
			}
		}

		if( check ){
			if( StringUtils.isEmpty( wrapIn.getEmpNo() )){
				Person person = userManagerService.getPersonObjByName( currentPerson.getDistinguishedName() );
				if( person != null ){
					if( StringUtils.isNotEmpty( person.getEmployee() )){
						attendanceDetailMobile.setEmpNo(person.getEmployee());
					}else{
						attendanceDetailMobile.setEmpNo(currentPerson.getDistinguishedName());
					}
				}
			}else{
				attendanceDetailMobile.setEmpNo( wrapIn.getEmpNo() );
			}
			attendanceDetailMobile.setEmpName( currentPerson.getDistinguishedName() );
			attendanceDetailMobile.setCheckin_time(wrapIn.getCheckin_time());
			attendanceDetailMobile.setCheckin_type(wrapIn.getCheckin_type());
		}

		if( check ){
			if( StringUtils.isNotEmpty(wrapIn.getSignTime()) ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getSignTime() );
					attendanceDetailMobile.setSignTime( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //打卡时间
				}catch( Exception e ){
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess( e, "员工手机打卡信息中打卡时间格式异常，格式:  HH:mm:ss. 时间：" + wrapIn.getSignTime() );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}
			}else{//打卡时间没有填写就填写为当前时间
				attendanceDetailMobile.setSignTime( dateOperation.getNowTime() ); //打卡时间
			}

			if( wrapIn.checkin_time < 1500000000000L ){ //无效
				attendanceDetailMobile.setCheckin_time( new Date().getTime() ); //打卡时间
			}else{//打卡时间没有填写就填写为当前时间
				attendanceDetailMobile.setCheckin_time( wrapIn.checkin_time ); //打卡时间
			}
		}
		if( check ){
			if( StringUtils.isNotEmpty( wrapIn.getRecordDateString() ) ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getRecordDateString() );
					attendanceDetailMobile.setRecordDate( datetime );
					attendanceDetailMobile.setRecordDateString( dateOperation.getDateStringFromDate( datetime, "yyyy-MM-dd") ); //打卡时间
				}catch( Exception e ){
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess( e,  "员工手机打卡信息中打卡日期格式异常，格式: yyyy-mm-dd. 日期：" + wrapIn.getRecordDateString() );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}				
			}else{
				attendanceDetailMobile.setRecordDateString( dateOperation.getNowDate() ); //打卡日期
			}
		}
		if( check ){
			if( StringUtils.isNotEmpty( wrapIn.getId() )){
				attendanceDetailMobile.setId( wrapIn.getId() );
			}
			attendanceDetailMobile.setSignDescription( wrapIn.getSignDescription() );
			try {
				attendanceDetailMobile = attendanceDetailServiceAdv.save( attendanceDetailMobile );
				result.setData( new Wo( attendanceDetailMobile.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在保存员工手机打卡信息时发生异常." );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}

		if( check ){
			//对该员工的所有移动考勤数据进行一个整合，并且立即send到分析队列
			attendanceDetailServiceAdv.pushToDetail( currentPerson.getDistinguishedName(), attendanceDetailMobile.getRecordDateString(), effectivePerson.getDebugger() );
		}
		return result;
	}
	
	public static class Wi {
		
		@FieldDescribe( "Id, 可以为空." )
		private String id;
		
		@FieldDescribe( "员工号, 可以为空." )
		private String empNo;

		@FieldDescribe( "员工姓名, 必须填写." )
		private String empName;

		@FieldDescribe( "打卡记录日期字符串：yyyy-mm-dd, 必须填写." )
		private String recordDateString;

		@FieldDescribe("打卡类型。字符串，目前有：上午上班打卡，上午下班打卡，下午上班打卡，下午下班打卡，外出打卡，午间打卡")
		private String checkin_type;

		@FieldDescribe("打卡时间。Unix时间戳")
		private long checkin_time;

		@FieldDescribe( "打卡时间: hh24:mi:ss, 必须填写." )
		private String signTime;

		@FieldDescribe( "打卡说明:上班打卡，下班打卡, 可以为空." )
		private String signDescription;

		@FieldDescribe( "其他说明备注, 可以为空." )
		private String description;

		@FieldDescribe( "打卡地点描述, 可以为空." )
		private String recordAddress = "未知";
		
		@FieldDescribe( "经度, 可以为空." )
		private String longitude;

		@FieldDescribe( "纬度, 可以为空." )
		private String latitude;

		@FieldDescribe( "操作设备类别：手机品牌|PAD|PC|其他, 可以为空." )
		private String optMachineType = "其他";

		@FieldDescribe( "操作设备类别：Mac|Windows|IOS|Android|其他, 可以为空." )
		private String optSystemName = "其他";

		public String getRecordDateString() {
			return recordDateString;
		}
		public void setRecordDateString(String recordDateString) {
			this.recordDateString = recordDateString;
		}
		public String getSignDescription() {
			return signDescription;
		}
		public void setSignDescription(String signDescription) {
			this.signDescription = signDescription;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getRecordAddress() {
			return recordAddress;
		}
		public void setRecordAddress(String recordAddress) {
			this.recordAddress = recordAddress;
		}
		public String getLongitude() {
			return longitude;
		}
		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
		public String getLatitude() {
			return latitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		public String getOptMachineType() {
			return optMachineType;
		}
		public void setOptMachineType(String optMachineType) {
			this.optMachineType = optMachineType;
		}
		public String getOptSystemName() {
			return optSystemName;
		}
		public void setOptSystemName(String optSystemName) {
			this.optSystemName = optSystemName;
		}
		public String getEmpNo() {
			return empNo;
		}
		public void setEmpNo(String empNo) {
			this.empNo = empNo;
		}
		public String getEmpName() {
			return empName;
		}
		public void setEmpName(String empName) {
			this.empName = empName;
		}
		public String getSignTime() {
			return signTime;
		}
		public void setSignTime(String signTime) {
			this.signTime = signTime;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getCheckin_type() { return checkin_type; }
		public void setCheckin_type(String checkin_type) { this.checkin_type = checkin_type; }
		public long getCheckin_time() { return checkin_time; }
		public void setCheckin_time(long checkin_time) { this.checkin_time = checkin_time; }
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}