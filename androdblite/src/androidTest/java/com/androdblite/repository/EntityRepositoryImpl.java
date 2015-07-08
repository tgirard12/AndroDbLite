package com.androdblite.repository;

import android.content.Context;

import com.androdblite.AndroDbLiteException;

/**
 * Created by tgirard on 30/06/15
 */
public class EntityRepositoryImpl extends DbRepositoryImpl implements EntityRepository {

    public EntityRepositoryImpl(Context context) throws AndroDbLiteException {
        super(context);
    }

}
