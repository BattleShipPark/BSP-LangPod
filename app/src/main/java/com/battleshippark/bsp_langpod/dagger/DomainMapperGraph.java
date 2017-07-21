package com.battleshippark.bsp_langpod.dagger;

import com.battleshippark.bsp_langpod.domain.DomainMapper;

import javax.inject.Singleton;

import dagger.Component;

/**
 */

@Component(modules = DomainMapperModule.class)
@Singleton
public interface DomainMapperGraph {
    DomainMapper domainMapper();
}
