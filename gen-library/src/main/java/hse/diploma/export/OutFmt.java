package hse.diploma.export;

import hse.diploma.enums.Container;
import hse.diploma.enums.props.PropKey;
import hse.diploma.enums.props.values.LineType;
import hse.diploma.model.VarDescriptor;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class OutFmt {
    public static String format(List<VarDescriptor> vars, Map<String,Object> ctx){
        StringBuilder sb=new StringBuilder();
        for(var v:vars){
            LineType lt=LineType.valueOf(
                    v.props().getOrDefault(PropKey.LINE_TYPE.key(),"single").toString().toUpperCase());
            Object val=ctx.get(v.name());
            if(v.container()== Container.SCALAR){
                sb.append(val).append('\n');
            }else{
                List<?> list=(List<?>)val;
                if(lt==LineType.SINGLE)
                    sb.append(list.stream().map(Object::toString).collect(Collectors.joining(" "))).append('\n');
                else list.forEach(e->sb.append(e).append('\n'));
            }
        }
        if(!sb.isEmpty()) sb.setLength(sb.length()-1);
        return sb.toString();
    }
}
