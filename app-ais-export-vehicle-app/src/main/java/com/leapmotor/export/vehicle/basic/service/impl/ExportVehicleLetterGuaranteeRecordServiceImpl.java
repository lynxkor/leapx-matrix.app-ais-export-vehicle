package com.leapmotor.export.vehicle.basic.service.impl;

import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeRecordDTOStructMapper;
import com.leapmotor.export.vehicle.basic.executor.cmd.ExportVehicleLetterGuaranteeRecordCmdExe;
import com.leapmotor.export.vehicle.basic.executor.query.ExportVehicleLetterGuaranteeRecordQryExe;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordAddOrUpdateReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordParam;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeRecordResp;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeRecordService;
import com.leapmotor.export.vehicle.cache.CustomerInfoCache;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 保函交易明细 服务实现类
 * </p>
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Service
public class ExportVehicleLetterGuaranteeRecordServiceImpl implements ExportVehicleLetterGuaranteeRecordService {


    private ExportVehicleLetterGuaranteeRecordCmdExe exportVehicleLetterGuaranteeRecordCmdExe;
    private ExportVehicleLetterGuaranteeRecordQryExe exportVehicleLetterGuaranteeRecordQryExe;
    private ExportVehicleLetterGuaranteeRecordGateway exportVehicleLetterGuaranteeRecordGateway;
    private ExportVehicleLetterGuaranteeGateway exportVehicleLetterGuaranteeGateway;
    private CustomerInfoCache customerInfoCache;

    @Override
    public ExportVehicleLetterGuaranteeRecordResp getExportVehicleLetterGuaranteeRecordById(String id) {
        ExportVehicleLetterGuaranteeRecordDo exportVehicleLetterGuaranteeRecordDo = exportVehicleLetterGuaranteeRecordGateway.getExportVehicleLetterGuaranteeRecordById(id);
        fillMainTableFields(exportVehicleLetterGuaranteeRecordDo);
        return ExportVehicleLetterGuaranteeRecordRespWithCustomer(exportVehicleLetterGuaranteeRecordDo);
    }

    @Override
    public List<ExportVehicleLetterGuaranteeRecordResp> listExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam) {
        ExportVehicleLetterGuaranteeRecordQo exportVehicleLetterGuaranteeRecordQo = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.paramToQuery(exportVehicleLetterGuaranteeRecordParam);
        List<ExportVehicleLetterGuaranteeRecordDo> exportVehicleLetterGuaranteeRecordDoList = exportVehicleLetterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordQo);
        fillMainTableFields(exportVehicleLetterGuaranteeRecordDoList);
        return ExportVehicleLetterGuaranteeRecordRespsWithCustomer(exportVehicleLetterGuaranteeRecordDoList);
    }

    @Override
    public PageResult<ExportVehicleLetterGuaranteeRecordResp> pageExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam) {
        PageResult<ExportVehicleLetterGuaranteeRecordDo> page = exportVehicleLetterGuaranteeRecordQryExe.pageExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordParam);
        fillMainTableFields(page.getRecords());
        PageResult<ExportVehicleLetterGuaranteeRecordResp> respPage = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainsToRespPage(page);
        fillCustomerInfo(respPage.getRecords());
        return respPage;
    }

    @Override
    public String addOrUpdateExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordAddOrUpdateReq exportVehicleLetterGuaranteeRecordAddOrUpdateReq) {
        ExportVehicleLetterGuaranteeRecordDo exportVehicleLetterGuaranteeRecordDo = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.addCmdToDomain(exportVehicleLetterGuaranteeRecordAddOrUpdateReq);
        return exportVehicleLetterGuaranteeRecordCmdExe.addOrUpdateExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordDo);
    }

    @Override
    public boolean deleteExportVehicleLetterGuaranteeRecordByIds(List<String> ids) {
        return exportVehicleLetterGuaranteeRecordCmdExe.deleteExportVehicleLetterGuaranteeRecordByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleLetterGuaranteeRecordByIds(LpBaseParam param) {
        return exportVehicleLetterGuaranteeRecordCmdExe.enableOrDisableExportVehicleLetterGuaranteeRecordByIds(param);
    }

    /**
     * 单条: 关联主表查询并填充 Do 上 5 个主表字段
     */
    private void fillMainTableFields(ExportVehicleLetterGuaranteeRecordDo recordDo) {
        if (recordDo == null || recordDo.getLgId() == null) {
            return;
        }
        ExportVehicleLetterGuaranteeDo main = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(recordDo.getLgId());
        applyMainFields(recordDo, main);
    }

    /**
     * 批量: 收集记录里所有 lgId,一次 IN 查询主表,按 id 建索引填充
     */
    private void fillMainTableFields(List<ExportVehicleLetterGuaranteeRecordDo> recordDos) {
        if (recordDos == null || recordDos.isEmpty()) {
            return;
        }
        Set<String> lgIds = new HashSet<>();
        for (ExportVehicleLetterGuaranteeRecordDo rec : recordDos) {
            if (rec.getLgId() != null) {
                lgIds.add(rec.getLgId());
            }
        }
        if (lgIds.isEmpty()) {
            return;
        }
        List<ExportVehicleLetterGuaranteeDo> mains = exportVehicleLetterGuaranteeGateway.listExportVehicleLetterGuaranteeByIds(lgIds);
        Map<String, ExportVehicleLetterGuaranteeDo> mainMap = mains.stream()
                .collect(Collectors.toMap(ExportVehicleLetterGuaranteeDo::getId, Function.identity(), (a, b) -> a));
        for (ExportVehicleLetterGuaranteeRecordDo rec : recordDos) {
            applyMainFields(rec, mainMap.get(rec.getLgId()));
        }
    }

    private void applyMainFields(ExportVehicleLetterGuaranteeRecordDo rec, ExportVehicleLetterGuaranteeDo main) {
        if (rec == null || main == null) {
            return;
        }
        rec.setLgStatus(main.getLgStatus());
        rec.setApproveTime(main.getApproveTime());
        rec.setTotalAmount(main.getTotalAmount());
        rec.setConversionRatio(main.getConversionRatio());
        rec.setAvailableAmount(main.getAvailableAmount());
        BigDecimal totalAmount = main.getTotalAmount();
        BigDecimal conversionRatio = main.getConversionRatio();
        if (totalAmount != null && conversionRatio != null) {
            rec.setConversionAmount(totalAmount.divide(conversionRatio, 2, RoundingMode.HALF_UP));
        }
    }

    // ============== 冗余字段填充(客户名称) ==============

    /**
     * 单条 Do → Resp,并按 customerId 查本地缓存填 customerCode/customerName。
     */
    private ExportVehicleLetterGuaranteeRecordResp ExportVehicleLetterGuaranteeRecordRespWithCustomer(ExportVehicleLetterGuaranteeRecordDo rec) {
        ExportVehicleLetterGuaranteeRecordResp resp = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainToResp(rec);
        fillCustomerInfo(resp);
        return resp;
    }

    /**
     * 批量 Do → Resp,同样填充客户冗余字段。
     */
    private List<ExportVehicleLetterGuaranteeRecordResp> ExportVehicleLetterGuaranteeRecordRespsWithCustomer(List<ExportVehicleLetterGuaranteeRecordDo> recs) {
        List<ExportVehicleLetterGuaranteeRecordResp> resps = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainsToResps(recs);
        fillCustomerInfo(resps);
        return resps;
    }

    /**
     * 单条 Resp 填充 customerCode/customerName(查本地缓存,未命中跳过)。
     */
    private void fillCustomerInfo(ExportVehicleLetterGuaranteeRecordResp resp) {
        if (resp == null || resp.getCustomerId() == null) {
            return;
        }
        CustomerInfoCache.Item item = customerInfoCache.getById(resp.getCustomerId());
        if (item != null) {
            resp.setCustomerCode(item.getCustomerCode());
            resp.setCustomerName(item.getCustomerName());
        }
    }

    /**
     * 批量 Resp 填充:收集 customerId → 一次 multiGet → 写回。
     */
    private void fillCustomerInfo(List<ExportVehicleLetterGuaranteeRecordResp> resps) {
        if (resps == null || resps.isEmpty()) {
            return;
        }
        Set<String> ids = new HashSet<>();
        for (ExportVehicleLetterGuaranteeRecordResp r : resps) {
            if (r != null && r.getCustomerId() != null) {
                ids.add(r.getCustomerId());
            }
        }
        if (ids.isEmpty()) {
            return;
        }
        Map<String, CustomerInfoCache.Item> map = customerInfoCache.getByIds(ids);
        if (map.isEmpty()) {
            return;
        }
        for (ExportVehicleLetterGuaranteeRecordResp r : resps) {
            if (r == null || r.getCustomerId() == null) {
                continue;
            }
            CustomerInfoCache.Item item = map.get(r.getCustomerId());
            if (item != null) {
                r.setCustomerCode(item.getCustomerCode());
                r.setCustomerName(item.getCustomerName());
            }
        }
    }
}
