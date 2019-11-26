<?php

namespace app\api\controller;
use think\Db;


class Index extends Base

{

    public function index()

    {
echo "ok";
    }

public  function  login(){
    $arr['code']=0;
    $arr['msg']='登录成功';
    $arr['token']=md5($_SESSION['token']);

    //获取员工信息
    $staff=Db::table('staff')
        ->field('real_name,company,department,room,last_time,icon')
        ->where('id',$_SESSION['user_id'])
        ->find();
    if($staff) {
        $mystaff['real_name'] = $staff['real_name'];
        $mystaff['last_time'] = $staff['last_time'];
        $mystaff['icon'] = $staff['icon'];
        $arr['staff'] = $mystaff;
//获取科室信息
$room=Db::table('room')
    ->field('name')
    ->where('id',$staff['room'])
    ->find();
        $arr['room']=$room;
//获取部门信息
        $department=Db::table('department')
            ->field('name')
            ->where('id',$staff['department'])
            ->find();
        $arr['department']=$department;
//获取公司信息
        $company=Db::table('company')
            ->field('name,address,icon')
            ->where('id',$staff['company'])
            ->find();
        $arr['company']=$company;

    }


    echo echojson($arr);
}

}

