create table metric(
  id bigserial primary key not null,
  type text not null,
  value bigint not null
);
create table property(
  id bigserial primary key not null,
  name text not null,
  value text not null
);
create table job_instance(
  id bigserial primary key not null,
  job_name text not null,
  jsl_name text not null,
  create_time timestamp not null default current_timestamp
);
create table job_execution(
  id bigserial primary key not null,
  job_instance_id int not null references job_instance(id) on delete cascade,
  previous_job_execution_id int references job_execution(id) on delete set null,
  job_name text not null,
  batch_status text not null,
  exit_status text,
  create_time timestamp not null default current_timestamp,
  start_time timestamp,
  updated_time timestamp,
  end_time timestamp,
  restart_element_id text
);
create table job_execution_history(
  id bigserial primary key not null,
  job_execution_id int not null references job_execution(id) on delete cascade,
  previous_job_execution_id int references job_execution(id) on delete cascade,
  unique (job_execution_id, previous_job_execution_id)
);
create table job_execution_property(
  id bigserial primary key not null,
  job_execution_id int not null references job_execution(id) on delete cascade,
  property_id int not null references property(id) on delete cascade
);
create table step_execution(
  id bigserial primary key not null,
  job_execution_id int not null references job_execution(id) on delete cascade,
  step_name text not null,
  batch_status text not null,
  exit_status text,
  create_time timestamp not null default current_timestamp,
  start_time timestamp,
  updated_time timestamp,
  end_time timestamp,
  reader_checkpoint oid,
  writer_checkpoint oid,
  persistent_user_data oid
);
create table step_execution_metric(
  id bigserial primary key not null,
  step_execution_id int not null references step_execution(id) on delete cascade,
  metric_id int not null references metric(id) on delete cascade,
  metric_type varchar not null
);
create table partition_execution(
  id bigserial primary key not null,
  step_execution_id int not null references step_execution(id) on delete cascade,
  partition_id int not null,
  batch_status text not null,
  exit_status text,
  create_time timestamp not null default current_timestamp,
  start_time timestamp,
  updated_time timestamp,
  end_time timestamp,
  reader_checkpoint oid,
  writer_checkpoint oid,
  persistent_user_data oid
);
create table partition_execution_property(
  id bigserial primary key not null,
  partition_execution_id int not null references partition_execution(id) on delete cascade,
  property_id int not null references property(id) on delete cascade
);
create table partition_execution_metric(
  id bigserial primary key not null,
  partition_execution_id int not null references partition_execution(id) on delete cascade,
  metric_id int not null references metric(id) on delete cascade,
  metric_type varchar not null
);