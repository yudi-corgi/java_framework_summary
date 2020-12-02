package com.demo.allframework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.allframework.entity.SysPermission;
import com.demo.allframework.mapper.SysPermMapper;
import com.demo.allframework.service.SysPermService;
import org.springframework.stereotype.Service;

/**
 * @author YUDI
 * @date 2020/11/23 23:21
 */
@Service
public class SysPermServiceImpl  extends ServiceImpl<SysPermMapper, SysPermission> implements SysPermService {
}
