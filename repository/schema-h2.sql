
create table if not exists metric(
  id bigint auto_increment primary key not null,
  type varchar not null,
  value bigint not null
);
create table if not exists property(
  id bigint auto_increment primary key not null,
  type varchar not null,
  value varchar not null
);
create table if not exists job_instance(
  id bigint auto_increment primary key not null,
  job_name varchar not null,
  jsl_name varchar not null,
  create_time timestamp not null default current_timestamp
);
create table if not exists job_execution(
  id bigint auto_increment primary key not null,
  job_instance_id int not null references job_instance(id) on delete cascade,
  job_name varchar not null,
  batch_status varchar not null,
  exit_status varchar,
  create_time timestamp not null default current_timestamp,
  start_time timestamp,
  updated_time timestamp,
  end_time timestamp,
  restart_element_id varchar
);
create table if not exists job_execution_history(
  id bigint auto_increment primary key not null,
  job_execution_id int not null references job_execution(id) on delete cascade,
  previous_job_execution_id int references job_execution(id) on delete cascade,
  unique (job_execution_id, previous_job_execution_id)
);
create table if not exists job_execution_property(
  id bigint auto_increment primary key not null,
  job_execution_id int not null references job_execution(id) on delete cascade,
  property_id int not null references property(id) on delete cascade
);
create table if not exists step_execution(
  id bigint auto_increment primary key not null,
  job_execution_id int not null references job_execution(id) on delete cascade,
  step_name varchar not null,
  batch_status varchar not null,
  exit_status varchar,
  create_time timestamp not null default current_timestamp,
  start_time timestamp,
  updated_time timestamp,
  end_time timestamp,
  reader_checkpoint bytea,
  writer_checkpoint bytea,
  persistent_user_data bytea
);
create table if not exists step_execution_metric(
  id bigint auto_increment primary key not null,
  step_execution_id int not null references step_execution(id) on delete cascade,
  metric_id int not null references metric(id) on delete cascade,
  metric_type varchar not null
);
create table if not exists partition_execution(
  id bigint auto_increment primary key not null,
  step_execution_id int not null references step_execution(id) on delete cascade,
  partition_id int not null,
  batch_status varchar not null,
  exit_status varchar,
  create_time timestamp not null default current_timestamp,
  start_time timestamp,
  updated_time timestamp,
  end_time timestamp,
  reader_checkpoint bytea,
  writer_checkpoint bytea,
  persistent_user_data bytea
);
create table if not exists partition_execution_property(
  id bigint auto_increment primary key not null,
  partition_execution_id int not null references partition_execution(id) on delete cascade,
  property_id int not null references property(id) on delete cascade
);
create table if not exists partition_execution_metric(
  id bigint auto_increment primary key not null,
  partition_execution_id int not null references partition_execution(id) on delete cascade,
  metric_id int not null references metric(id) on delete cascade,
  metric_type varchar not null
);