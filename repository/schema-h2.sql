
create sequence metric_id_seq start with 1 increment by 1 no minvalue no maxvalue cache 1;
create sequence property_id_seq start with 1 increment by 1 no minvalue no maxvalue cache 1;
create sequence job_instance_id_seq start with 1 increment by 1 no minvalue no maxvalue cache 1;
create sequence job_execution_id_seq start with 1 increment by 1 no minvalue no maxvalue cache 1;
create sequence job_execution_property_id_seq start with 1 increment by 1 no minvalue no maxvalue cache 1;
create sequence step_execution_id_seq start with 1 increment by 1 no minvalue no maxvalue cache 1;
create sequence step_execution_metric_id_seq start with 1 increment by 1 no minvalue no maxvalue cache 1;
create sequence partition_execution_id_seq start with 1 increment by 1 no minvalue no maxvalue cache 1;
create sequence partition_execution_property_id_seq start with 1 increment by 1 no minvalue no maxvalue cache 1;
create sequence partition_execution_metric_id_seq start with 1 increment by 1 no minvalue no maxvalue cache 1;

create table public.metric(
  id bigint default nextval('metric_id_seq') primary key not null,
  type varchar not null,
  value bigint not null
);
create table public.property(
  id bigint default nextval('property_id_seq') primary key not null,
  key varchar not null,
  value varchar not null
);
create table public.job_instance(
  id bigint default nextval('job_instance_id_seq') primary key not null,
  job_name varchar not null,
  jsl_name varchar not null
);
create table public.job_execution(
  id bigint default nextval('job_execution_id_seq') primary key not null,
  job_instance_id int not null references public.job_instance(id) on delete cascade,
  previous_job_execution_id int references public.job_execution(id) on delete set null,
  job_name varchar not null,
  batch_status varchar not null,
  exit_status varchar,
  create_time timestamp not null default current_timestamp,
  start_time timestamp,
  updated_time timestamp,
  end_time timestamp,
  restart_element_id varchar
);
create table public.job_execution_property(
  id bigint default nextval('job_execution_property_id_seq') primary key not null,
  job_execution_id int not null references public.job_execution(id) on delete cascade,
  property_id int not null references public.property(id) on delete cascade
);
create table public.step_execution(
  id bigint default nextval('step_execution_id_seq') primary key not null,
  job_execution_id int not null references public.job_execution(id) on delete cascade,
  step_name varchar not null,
  batch_status varchar not null,
  exit_status varchar,
  create_time timestamp not null default current_timestamp,
  start_time timestamp not null,
  updated_time timestamp,
  end_time timestamp,
  reader_checkpoint bytea,
  writer_checkpoint bytea,
  persistent_user_data bytea
);
create table public.step_execution_metric(
  id bigint default nextval('step_execution_metric_id_seq') primary key not null,
  step_execution_id int not null references public.step_execution(id) on delete cascade,
  metric_id int not null references public.metric(id) on delete cascade,
  metric_type varchar not null
);
create table public.partition_execution(
  id bigint default nextval('partition_execution_id_seq') primary key not null,
  step_execution_id int not null references public.step_execution(id) on delete cascade,
  partition_id int not null,
  batch_status varchar not null,
  exit_status varchar,
  create_time timestamp not null default current_timestamp,
  start_time timestamp not null,
  updated_time timestamp,
  end_time timestamp,
  reader_checkpoint bytea,
  writer_checkpoint bytea,
  persistent_user_data bytea
);
create table public.partition_execution_property(
  id bigint default nextval('partition_execution_property_id_seq') primary key not null,
  partition_execution_id int not null references public.partition_execution(id) on delete cascade,
  property_id int not null references public.property(id) on delete cascade
);
create table public.partition_execution_metric(
  id bigint default nextval('partition_execution_metric_id_seq') primary key not null,
  partition_execution_id int not null references public.partition_execution(id) on delete cascade,
  metric_id int not null references public.metric(id) on delete cascade,
  metric_type varchar not null
);