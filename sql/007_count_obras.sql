use obracontrol;

select
	ob.status,
    COUNT(ob.status) total_aberto
from 
	obra ob
where (ob.status = 'aberta')
group by status
;
    
    
select
	ob.status,
    COUNT(ob.status) total_aberto
from 
	obra ob
where (ob.status = 'finalizada')
group by status
;
    