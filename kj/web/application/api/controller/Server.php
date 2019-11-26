<?php
/**
 * Created by PhpStorm.
 * User: XU
 * Date: 2019/10/2
 * Time: 17:08
 */

namespace app\api\controller;


use think\Controller;
use think\Db;
use think\Log;
class Server extends  Controller
{
public  function  get_server(){




if(isset($_POST['server_name'])){
$server=Db::table('oa_product')
    ->field('name,company,server_home,time_out')
    ->where('name',$_POST['server_name'])
    ->find();
    if($server){
        $time=date('Y-m-d H:i:s',time());
        if($server['time_out']<$time){
            echo echojson(config('code.code6'));
        }else{
            $server['code']=0;
            $server['msg']="服务器获取成功";
            echo echojson( $server);
        }



    }else{
        echo echojson(config('code.code5'));
    }
}else{
echo echojson(config('code.code4'));
}
}
}