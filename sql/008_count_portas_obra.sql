use obracontrol;

select
	ob.nome,
    COUNT(po.id) portas
from 
	porta po
	inner join apartamento ap
		on ap.id = po.apartamento_id
	inner join obra ob
		on ob.id = ap.obra_id
group by
	ob.nome