package io.therapistai.memory.domain;

import java.util.EnumSet;
import java.util.Set;

public enum MemoryType {

    PERSON(
            ConstraintType.EVOLUTIONARY,
            EnumSet.of(
                    MemoryKey.NAME,
                    MemoryKey.AGE,
                    MemoryKey.DATE_OF_BIRTH,
                    MemoryKey.GENDER,
                    MemoryKey.NATIONALITY,
                    MemoryKey.LANGUAGE,
                    MemoryKey.ROLE,
                    MemoryKey.DESCRIPTION
            )
    ),

    WORK_STATUS(
            ConstraintType.UNIQUE,
            EnumSet.of(
                    MemoryKey.EMPLOYMENT_STATUS,
                    MemoryKey.JOB_TITLE,
                    MemoryKey.COMPANY,
                    MemoryKey.INDUSTRY,
                    MemoryKey.WORK_MODE,
                    MemoryKey.UNEMPLOYMENT_DURATION,
                    MemoryKey.JOB_SEARCH_STATUS,
                    MemoryKey.WORK_STRESS,
                    MemoryKey.CAREER_CONCERN
            )
    ),

    SCHOOL_STATUS(
            ConstraintType.UNIQUE,
            EnumSet.of(
                    MemoryKey.EDUCATION_STATUS,
                    MemoryKey.SCHOOL_NAME,
                    MemoryKey.FIELD_OF_STUDY,
                    MemoryKey.ACADEMIC_LEVEL,
                    MemoryKey.ACADEMIC_CONCERN
            )
    ),

    FINANCIAL_STATUS(
            ConstraintType.UNIQUE,
            EnumSet.of(
                    MemoryKey.FINANCIAL_SITUATION,
                    MemoryKey.DEBT_STATUS,
                    MemoryKey.INCOME_STATUS,
                    MemoryKey.FINANCIAL_CONCERN,
                    MemoryKey.FINANCIAL_DEPENDENCY
            )
    ),

    LIVING_SITUATION(
            ConstraintType.UNIQUE,
            EnumSet.of(
                    MemoryKey.COUNTRY,
                    MemoryKey.CITY,
                    MemoryKey.HOUSEHOLD,
                    MemoryKey.LIVING_ARRANGEMENT,
                    MemoryKey.HOUSING_CONCERN
            )
    ),

    RELATIONSHIP_STATUS(
            ConstraintType.UNIQUE,
            EnumSet.of(
                    MemoryKey.MARITAL_STATUS,
                    MemoryKey.PARTNERSHIP_STATUS,
                    MemoryKey.RELATIONSHIP_QUALITY,
                    MemoryKey.CURRENT_CONFLICT,
                    MemoryKey.SEPARATION_STATUS
            )
    ),

    SUPPORT_SYSTEM(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.SUPPORT_PERSON,
                    MemoryKey.SUPPORT_ROLE,
                    MemoryKey.SUPPORT_TYPE,
                    MemoryKey.SUPPORT_AVAILABILITY
            )
    ),

    PREFERENCE(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.PREFERENCE_TYPE,
                    MemoryKey.PREFERENCE_VALUE
            )
    ),

    LIFE_EVENT(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.EVENT_TYPE,
                    MemoryKey.EVENT_DATE,
                    MemoryKey.DESCRIPTION,
                    MemoryKey.IMPACT,
                    MemoryKey.STATUS
            )
    ),

    CHILDHOOD_BACKGROUND(
            ConstraintType.EVOLUTIONARY,
            EnumSet.of(
                    MemoryKey.FAMILY_STRUCTURE,
                    MemoryKey.PARENT_RELATIONSHIP,
                    MemoryKey.CHILDHOOD_EVENT,
                    MemoryKey.CHILDHOOD_DESCRIPTION
            )
    ),

    CORE_BELIEF(
            ConstraintType.EVOLUTIONARY,
            EnumSet.of(
                    MemoryKey.BELIEF,
                    MemoryKey.BELIEF_DOMAIN
            )
    ),

    IDENTITY(
            ConstraintType.EVOLUTIONARY,
            EnumSet.of(
                    MemoryKey.IDENTITY_STATEMENT,
                    MemoryKey.IDENTITY_DOMAIN,
                    MemoryKey.SELF_DESCRIPTION
            )
    ),

    THERAPY_GOAL(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.GOAL,
                    MemoryKey.GOAL_PRIORITY,
                    MemoryKey.GOAL_PROGRESS
            )
    ),

    THERAPY_TOPIC(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.TOPIC,
                    MemoryKey.TOPIC_FREQUENCY,
                    MemoryKey.TOPIC_IMPORTANCE
            )
    ),

    THERAPY_EXPECTATION(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.EXPECTATION,
                    MemoryKey.EXPECTATION_PRIORITY
            )
    ),

    ADAPTIVE_COPING(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.COPING_STRATEGY,
                    MemoryKey.COPING_EFFECTIVENESS,
                    MemoryKey.COPING_FREQUENCY
            )
    ),

    MALADAPTIVE_COPING(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.COPING_STRATEGY,
                    MemoryKey.COPING_CONSEQUENCE,
                    MemoryKey.COPING_FREQUENCY
            )
    ),

    SYMPTOM(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.SYMPTOM_NAME,
                    MemoryKey.SEVERITY,
                    MemoryKey.FREQUENCY,
                    MemoryKey.DURATION
            )
    ),

    TRIGGER(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.TRIGGER_SOURCE,
                    MemoryKey.TRIGGER_CONTEXT,
                    MemoryKey.TRIGGER_INTENSITY
            )
    ),

    FEAR(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.FEAR_OBJECT,
                    MemoryKey.FEAR_CONTEXT,
                    MemoryKey.FEAR_INTENSITY
            )
    ),

    STRESSOR(
            ConstraintType.ACCUMULATIVE,
            EnumSet.of(
                    MemoryKey.STRESSOR_SOURCE,
                    MemoryKey.STRESSOR_CONTEXT,
                    MemoryKey.STRESS_LEVEL
            )
    );

    private final ConstraintType constraintType;
    private final Set<MemoryKey> allowedKeys;

    MemoryType(ConstraintType constraintType, Set<MemoryKey> allowedKeys) {
        this.constraintType = constraintType;
        this.allowedKeys = Set.copyOf(allowedKeys);
    }

    public ConstraintType constraintType() {
        return constraintType;
    }

    public boolean allows(MemoryKey key) {
        return key != null && allowedKeys.contains(key);
    }

    public boolean isUnique() {
        return constraintType == ConstraintType.UNIQUE;
    }

    public boolean isAccumulative() {
        return constraintType == ConstraintType.ACCUMULATIVE;
    }

    public boolean isEvolutionary() {
        return constraintType == ConstraintType.EVOLUTIONARY;
    }
}