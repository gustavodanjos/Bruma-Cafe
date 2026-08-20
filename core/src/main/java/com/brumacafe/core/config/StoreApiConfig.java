package com.brumacafe.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "Bruma Café - Configuração da API da Loja",
    description = "Configuração do endpoint e limites para o catálogo de produtos"
)
public @interface StoreApiConfig {

    @AttributeDefinition(
        name = "URL do Catálogo",
        description = "Endereço HTTP público da API da loja",
        type = AttributeType.STRING
    )
    String apiUrl() default "https://dummyjson.com/products";

    @AttributeDefinition(
        name = "Quantidade de Produtos",
        description = "Limite de produtos trazidos da API",
        type = AttributeType.INTEGER
    )
    int productLimit() default 4;

    @AttributeDefinition(
        name = "Timeout de Conexão (ms)",
        description = "Tempo limite para conexão e leitura em milissegundos",
        type = AttributeType.INTEGER
    )
    int connectionTimeout() default 5000;

    @AttributeDefinition(
        name = "Cache TTL (segundos)", 
        description = "Tempo em segundos que os produtos ficam em memória antes de nova consulta à API", 
        type = AttributeType.INTEGER
    )
    int cacheTtlSeconds() default 300;
}