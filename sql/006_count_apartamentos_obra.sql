use obracontrol;

select
	ob.id,
	ob.nome,
    cd.nome,
    es.sigla,
    COUNT(ap.id) apartamentos
from 
	apartamento ap
	inner join obra ob
		on ob.id = ap.obra_id
	inner join cidade cd
		on cd.id = ob.cidade_id
	inner join estado es
		on es.id = cd.estado_id
group by
	ob.id,
	ob.nome,
	cd.nome,
    es.sigla