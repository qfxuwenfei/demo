<?php
/**
 * Created by PhpStorm.
 * User: XU
 * Date: 2019/10/2
 * Time: 13:45
 */

namespace app\api\controller;


use think\Controller;
use think\Db;
use think\Log;

class Base extends Controller
{
    public function _initialize()
    {
        parent::_initialize();
        $this->check();
    }
    protected function check(){
      session_start();
        $is_login=false;
        //如果有用户名密码，则登录
        if(isset($_POST['login_name']) && isset($_POST['login_pwd'])){
            $name=$_POST['login_name'];
            $pwd=$_POST['login_pwd'];
            $myinfo=Db::table('staff')
                ->field('id,login_pwd')
                ->where('login_name',$name)
                ->find();
            if($myinfo){
                //如果登录成功
                if($pwd==$myinfo['login_pwd']){
                    $time=time();
                    $up['last_time']=date('Y-m-d H:i:s',$time);
                    $mytoken='OA-'.$myinfo['id'].'-'.$time;
                    $up['token']=$mytoken;
                    $re=Db::name('staff')
                        ->where('id',$myinfo['id'])
                        ->update($up);
                    $_SESSION['token']=$mytoken;
                    $_SESSION['user_id']=$myinfo['id'];
                    $is_login=true;
                    //登录成功
                }
//密码错误
                else{

                    echo echojson(config('code.code3'));
                    exit;
                }

            }
            //没有查找到用户
            else{
                echo echojson(config('code.code3'));
                exit;
            }
        }





        if(!$is_login) {
//如果已登录，判断登录信息是否正确
            if (isset($_SESSION['token']) && isset($_POST['token'])) {
if(md5($_SESSION['token'])!=$_POST['token']){
    echo echojson(config('code.code1'));
    exit;
}
            }
            //登录超时
            else{
                echo echojson(config('code.code1'));
                exit;
            }
        }

    }
}