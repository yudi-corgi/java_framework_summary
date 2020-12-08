package com.demo.allframework.uaa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.allframework.uaa.entity.SysPermission;
import com.demo.allframework.uaa.mapper.SysPermMapper;
import com.demo.allframework.uaa.service.SysPermService;
import org.springframework.stereotype.Service;

/**
 * @author YUDI
 * @date 2020/11/23 23:21
 */
@Service
public class SysPermServiceImpl  extends ServiceImpl<SysPermMapper, SysPermission> implements SysPermService {
}
