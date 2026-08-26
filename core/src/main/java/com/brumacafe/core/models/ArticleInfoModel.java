package com.brumacafe.core.models;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface ArticleInfoModel {

    String getFormattedDate();
    int getReadingTime();
}
