package net.newploy.payroll.batch.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPeople is a Querydsl query type for People
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPeople extends EntityPathBase<People> {

    private static final long serialVersionUID = -1317494300L;

    public static final QPeople people = new QPeople("people");

    public final NumberPath<Integer> enabled = createNumber("enabled", Integer.class);

    public final StringPath firstName = createString("firstName");

    public final StringPath lastName = createString("lastName");

    public final NumberPath<Long> personId = createNumber("personId", Long.class);

    public QPeople(String variable) {
        super(People.class, forVariable(variable));
    }

    public QPeople(Path<? extends People> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPeople(PathMetadata metadata) {
        super(People.class, metadata);
    }

}

