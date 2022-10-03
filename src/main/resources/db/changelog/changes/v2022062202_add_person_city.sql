ALTER TABLE ulab_edu.person ADD COLUMN city VARCHAR(50) DEFAULT '';

COMMENT ON COLUMN ulab_edu.person.city is 'Город проживания пользователя';