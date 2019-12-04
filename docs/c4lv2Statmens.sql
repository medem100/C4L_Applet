#insert

insert into setup (name,description)
values('Default','Default Setup');


insert into scene (name,description)
values('first', 'First scene in the new DB');

insert into device_category (name,description)
values ('default',' default device Category');

insert into device (permutation,description,name,start_address,virtual_dimmer_channel,virtual_dimming,Device_category_id,rotary_channels)
values('0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15','First device','First',0,8,'0,1,2',1,'1,2');

insert into device (permutation,description,name,start_address,virtual_dimmer_channel,virtual_dimming,Device_category_id,rotary_channels)
values('0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15','Secend device','Secend',16,8,'0,1,2',1,'1,2');

insert into device (permutation,description,name,start_address,virtual_dimmer_channel,virtual_dimming,Device_category_id,rotary_channels)
values('0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15','Thirde device','Thirde',32,8,'0,1,2',1,'1,2');

insert into device_status (input,Device_id)
values('100,150,0,0,0,0,0,0,0,0,0,0,0,0,0,0',1);

insert into device_status (input,Device_id)
values('150,0,200,0,0,0,0,0,0,0,0,0,0,0,0,0',3);

insert into device_status (input,Device_id)
values('150,50,200,0,0,0,0,0,0,0,0,0,0,0,0,0',4);

insert into effect ( name,id,category,description)
values('WILD','10','Random','Move to random spots (range size)'),
('JUMP','11','Random','Jump to random spots (range size)'),
('SINUS','00','Simpel','Add a sine-wave of amplitude size/2'),
('RAMP','01','Simpel','linear fade from -size/2 to +size/2, jump back'),
('REVRAMP','02','Simpel','linear fade from +size/2 to -size/2, jump back'),
('LINEAR','03','Simpel','linear fade from +size/2 to -size/2, fade back'),
('LINEAR_HOLDL','04','Simpel','Description like linear, but pausing at low-point'),
('LINEAR_HOLDH','05','Simpel','Description like linear, but pausing at high-point'),
('STROBO','06','Simpel','Channel is active size/256 parts of effect time, zero afterwards'),
('STROBO_HOLD','07','Simpel','Strobo without changing colors when on'),
('CIRCLE','08','Simpel','sine on first channel, cosine on second'),
('RAINBOW','09','Simpel','fade through a rainbow (assuming 1,2,3 as RGB)');

insert into effect_status(size,speed,channels,accept_input,state,Device_status_id,Effect_id)
values(100,50,'1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0','true',0,8,'08'),
(150,50,'1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0','true',100,9,'00'),
(100,50,'1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0','true',0,10,'08');

insert into scene_has_device_status(Scene_id,Device_status_id)
value(1,8),
(1,9),
(1,10);

insert into setup_has_scene(SetUp_id,Scene_id)
values(1,1);

/*********************************************************************************************************************************/
#select



/*
select ds.id, ds.input , s.description from device_status ds
inner join scene_has_device_status shds
on shds.Device_status_id = ds.id
inner join scene s
on shds.Scene_id = s.id
where s.id = 1;

select ds.id, ds.input , s.description from device_status ds
inner join effect_status es
on ds.id = es.Device_status_id
inner join device d
on ds.Device_id = d.id
inner join scene_has_device_status shds
on shds.Device_status_id = ds.id
inner join scene s
on shds.Scene_id = s.id
where s.id = 1; */

select ds.id, ds.input , d.description from device_status ds
inner join effect_status es
on ds.id = es.Device_status_id
inner join device d
on ds.Device_id = d.id
inner join scene_has_device_status shds
on ds.id = shds.Device_status_id
where shds.Scene_id = 1;







