import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static java.lang.System.exit;

// Hello again and again and again

public class processRawStockData {
    private static final String MINING_DATA_FILE_NAME = "miningStockAnalysisData.csv";
    private static final String COMMA_DELIMITER = ",";
    private static final String TAB_DELIMITER = "\t";
    private static final int MAX_PARAMETERS = 500;
    private static boolean headingFlag = false;
    private static String[] headingLine;
    private static final int COMMA = 0;
    private static final int TAB = 1;

    private static final int NO              = 0;
    private static final int SYMBOL          = 1;
    private static final int COMPANY_NAME    = 2;
    private static final int MARKET_CAP      = 3;
    private static final int STOCK_PRICE     = 4;
    private static final int PERCENT_CHANGE  = 5;
    private static final int REVENUE         = 6;
    private static final int VOLUME          = 7;
    private static final int EMPLOYEES       = 8;
    private static final int FOUNDED         = 9;
    private static final int GROSS_PROFIT    = 10;
    private static final int REV_GROWTH      = 11;
    private static final int OP_INCOME       = 12;
    private static final int NET_INCOME      = 13;
    private static final int TOTAL_CASH      = 14;
    private static final int TOTAL_DEBT      = 15;
    private static final int DIV             = 16;
    private static final int LAST_DIV        = 17;
    private static final int SHORT_RATIO     = 18;
    private static final int ASSETS          = 19;
    private static final int SHARES          = 20;
    private static final int EXCHANGE        = 21;
    private static final int SHARES_INSIDERS = 22;
    private static final int SHARES_INSTUT   = 23;

    // not needed
    private static final String[] outputCsvOrder = {
            "No.",
            "Symbol",
            "Company Name",
            "Market Cap",
            "Stock Price",
            "% Change",
            "Revenue",
            "Volume",
            "Employees",
            "Founded",
            "Gross Profit",
            "Rev. Growth",
            "Op. Income",
            "Net Income",
            "Total Cash",
            "Total Debt",
            "Div. ($)",
            "Last Div.",
            "Short Ratio",
            "Assets",
            "Shares",
            "Exchange",
            "Shares Insiders",
            "Shares Institut."};

    private static final int EXCHANGE_FULL_NAME = 0;
    private static final int EXCHANGE_TICKER = 1;
    private static final String[][] exchangeTickerRelation = {
            {"Toronto Stock Exchange", "TSX"},
            {"TSX Venture Exchange", "TSXV"}};

    private static final String[] miningCompanySymbols = { "TSX:SMC" ,"TSX:AUMN" ,"TSX:ELEF" ,"TSX:SVB"
            ,"TSX:ASND" ,"TSX:MSV" ,"TSX:SAM" ,"TSX:AVL" ,"TSX:YRB" ,"TSX:WRX" ,"TSX:AMM" ,"TSX:EXN"
            ,"TSX:GRC" ,"TSX:NCF" ,"TSX:FT" ,"TSX:BKI" ,"TSX:ELR" ,"TSX:COPR" ,"TSX:ESM" ,"TSX:NVO"
            ,"TSX:RTG" ,"TSX:DIAM" ,"TSX:GENM" ,"TSX:ATCU" ,"TSX:OGD" ,"TSX:TI" ,"TSX:TSK" ,"TSX:S"
            ,"TSX:NEXT" ,"TSX:CCM" ,"TSX:WM" ,"TSX:ORV" ,"TSX:SLR" ,"TSX:GMX" ,"TSX:ELO" ,"TSX:GCU"
            ,"TSX:FURY" ,"TSX:BSX" ,"TSX:G" ,"TSX:TLO" ,"TSX:SAU" ,"TSX:XAM" ,"TSX:LN" ,"TSX:XTG"
            ,"TSX:ARA" ,"TSX:LGD" ,"TSX:STLR" ,"TSX:TRX" ,"TSX:GEO" ,"TSX:VGZ" ,"TSX:FSY" ,"TSX:LGO"
            ,"TSX:FF" ,"TSX:AOT" ,"TSX:ITH" ,"TSX:FDY" ,"TSX:TLG" ,"TSX:AMC" ,"TSX:PTM" ,"TSX:STGO"
            ,"TSX:LUC" ,"TSX:GLO" ,"TSX:DC.A" ,"TSX:SMT" ,"TSX:FAR" ,"TSX:DNG" ,"TSX:PRYM" ,"TSX:SBI"
            ,"TSX:MNO" ,"TSX:APM" ,"TSX:VOXR" ,"TSX:JAG" ,"TSX:GOLD" ,"TSX:NUAG" ,"TSX:ECOR" ,"TSX:ERD"
            ,"TSX:LIRC" ,"TSX:WRN" ,"TSX:ARG" ,"TSX:ASCU" ,"TSX:ASM" ,"TSX:PMET" ,"TSX:PRB" ,"TSX:IAU"
            ,"TSX:SOLG" ,"TSX:TMQ" ,"TSX:ABRA" ,"TSX:USA" ,"TSX:FVL" ,"TSX:III" ,"TSX:GAU" ,"TSX:FFM"
            ,"TSX:ETG" ,"TSX:ORE" ,"TSX:LAR" ,"TSX:MND" ,"TSX:MARI" ,"TSX:GGD" ,"TSX:MUX" ,"TSX:AII"
            ,"TSX:MSA" ,"TSX:MDI" ,"TSX:SLS" ,"TSX:NDM" ,"TSX:DSV" ,"TSX:GTWO" ,"TSX:VZLA" ,"TSX:LAC"
            ,"TSX:TKO" ,"TSX:IE" ,"TSX:PPTA" ,"TSX:RUP" ,"TSX:SVM" ,"TSX:CNL" ,"TSX:ALS" ,"TSX:ARIS"
            ,"TSX:AYA" ,"TSX:EDR" ,"TSX:NG" ,"TSX:FOM" ,"TSX:AAUC" ,"TSX:SEA" ,"TSX:ERO" ,"TSX:SKE"
            ,"TSX:CG" ,"TSX:ORA" ,"TSX:MAG" ,"TSX:WGX" ,"TSX:FVI" ,"TSX:CXB" ,"TSX:WDO" ,"TSX:NGEX"
            ,"TSX:SSRM" ,"TSX:KNT" ,"TSX:SSL" ,"TSX:DPM" ,"TSX:OGC" ,"TSX:TXG" ,"TSX:NGD" ,"TSX:HBM"
            ,"TSX:CGG" ,"TSX:GMIN" ,"TSX:OLA" ,"TSX:PRU" ,"TSX:AG" ,"TSX:EQX" ,"TSX:IMG" ,"TSX:ELD"
            ,"TSX:BTO" ,"TSX:OR" ,"TSX:TFPM" ,"TSX:EDV" ,"TSX:LUN" ,"TSX:LUG" ,"TSX:PAAS" ,"TSX:FM"
            ,"TSX:IVN" ,"TSX:AGI" ,"TSX:K" ,"TSX:TECK.A" ,"TSX:TECK.B" ,"TSX:FNV" ,"TSX:ABX" ,"TSX:WPM"
            ,"TSX:NGT" ,"TSX:AEM" ,"TSXV:ARTG" ,"TSXV:SGD" ,"TSXV:MAU" ,"TSXV:SXGC" ,"TSXV:AFM" ,"TSXV:AMRQ"
            ,"TSXV:ATX" ,"TSXV:RBX" ,"TSXV:FDR" ,"TSXV:RML" ,"TSXV:IFOS" ,"TSXV:MTA" ,"TSXV:RIO" ,"TSXV:SFR"
            ,"TSXV:ELE" ,"TSXV:EMO" ,"TSXV:MKO" ,"TSXV:ITR" ,"TSXV:EMX" ,"TSXV:ZDC" ,"TSXV:THX" ,"TSXV:LUCA"
            ,"TSXV:OGN" ,"TSXV:NFG" ,"TSXV:DV" ,"TSXV:ODV" ,"TSXV:ALDE" ,"TSXV:FWZ" ,"TSXV:PNPN" ,"TSXV:NICU"
            ,"TSXV:BRVO" ,"TSXV:REG" ,"TSXV:LUM" ,"TSXV:GRZ" ,"TSXV:OM" ,"TSXV:CAD" ,"TSXV:OMG" ,"TSXV:HSTR"
            ,"TSXV:LA" ,"TSXV:WRLG" ,"TSXV:GOT" ,"TSXV:CNC" ,"TSXV:SIG" ,"TSXV:MAI" ,"TSXV:MFG" ,"TSXV:NCX"
            ,"TSXV:PREM" ,"TSXV:CUSN" ,"TSXV:TAU" ,"TSXV:HAN" ,"TSXV:GGA" ,"TSXV:DBG" ,"TSXV:SCZ" ,"TSXV:NCAU"
            ,"TSXV:GQC" ,"TSXV:CUU" ,"TSXV:PWM" ,"TSXV:TUD" ,"TSXV:CVV" ,"TSXV:BIG" ,"TSXV:GPH" ,"TSXV:CDPR"
            ,"TSXV:MMY" ,"TSXV:IBAT" ,"TSXV:GHRT" ,"TSXV:AAG" ,"TSXV:CNRI" ,"TSXV:FISH" ,"TSXV:MOON" ,"TSXV:RDS"
            ,"TSXV:MSR" ,"TSXV:CVW" ,"TSXV:AMX" ,"TSXV:QTWO" ,"TSXV:LGC" ,"TSXV:SLVR" ,"TSXV:AU" ,"TSXV:AHR"
            ,"TSXV:NKG" ,"TSXV:FNM" ,"TSXV:LIFT" ,"TSXV:GSHR" ,"TSXV:BRC" ,"TSXV:LME" ,"TSXV:KLD" ,"TSXV:CRE"
            ,"TSXV:MMA" ,"TSXV:NEXG" ,"TSXV:MJS" ,"TSXV:HCU" ,"TSXV:PML" ,"TSXV:MKA" ,"TSXV:NOAL" ,"TSXV:OCO"
            ,"TSXV:SUP" ,"TSXV:SM" ,"TSXV:NBM" ,"TSXV:CBR" ,"TSXV:BYN" ,"TSXV:LI" ,"TSXV:LIO" ,"TSXV:UCU"
            ,"TSXV:HPQ" ,"TSXV:TDG" ,"TSXV:HI" ,"TSXV:MAE" ,"TSXV:AE" ,"TSXV:FPX" ,"TSXV:VEIN" ,"TSXV:ARK"
            ,"TSXV:GSVR" ,"TSXV:CKG" ,"TSXV:HMR" ,"TSXV:OMI" ,"TSXV:KRY" ,"TSXV:WVM" ,"TSXV:HCH" ,"TSXV:BZ"
            ,"TSXV:APGO" ,"TSXV:PLSR" ,"TSXV:DEF" ,"TSXV:OCG" ,"TSXV:SYH" ,"TSXV:VIO" ,"TSXV:RVG" ,"TSXV:VROY"
            ,"TSXV:SVRS" ,"TSXV:CYG" ,"TSXV:NIM" ,"TSXV:MFL" ,"TSXV:BMM" ,"TSXV:SOMA" ,"TSXV:VML" ,"TSXV:SSV"
            ,"TSXV:PALI" ,"TSXV:ALEX" ,"TSXV:FPC" ,"TSXV:DLP" ,"TSXV:ETL" ,"TSXV:LRA" ,"TSXV:NKL" ,"TSXV:KTN"
            ,"TSXV:SVE" ,"TSXV:MLP" ,"TSXV:BOGO" ,"TSXV:MGG" ,"TSXV:EMPR" ,"TSXV:CCCM" ,"TSXV:BNKR" ,"TSXV:FLCN"
            ,"TSXV:SPA" ,"TSXV:BCM" ,"TSXV:TSG" ,"TSXV:GFG" ,"TSXV:ADY" ,"TSXV:CGC" ,"TSXV:MGRO" ,"TSXV:WMK"
            ,"TSXV:GRSL" ,"TSXV:CERT" ,"TSXV:NILI" ,"TSXV:ANK" ,"TSXV:ESK" ,"TSXV:FMT" ,"TSXV:MSG" ,"TSXV:CAPT"
            ,"TSXV:ECU" ,"TSXV:ABI" ,"TSXV:SCOT" ,"TSXV:IPT" ,"TSXV:BRW" ,"TSXV:QZM" ,"TSXV:NWST" ,"TSXV:TUO"
            ,"TSXV:WGO" ,"TSXV:LTC" ,"TSXV:AZM" ,"TSXV:RAMP" ,"TSXV:CDB" ,"TSXV:GLB" ,"TSXV:ECR" ,"TSXV:AMK"
            ,"TSXV:AGAG" ,"TSXV:GWM" ,"TSXV:SRR" ,"TSXV:RUA" ,"TSXV:ZNG" ,"TSXV:DMX" ,"TSXV:GPAC" ,"TSXV:ARIC"
            ,"TSXV:DEFN" ,"TSXV:GSP" ,"TSXV:LEM" ,"TSXV:IGO" ,"TSXV:SALT" ,"TSXV:WAM" ,"TSXV:VLC" ,"TSXV:AUMB"
            ,"TSXV:PGE" ,"TSXV:BTR" ,"TSXV:MOG" ,"TSXV:EQTY" ,"TSXV:SSVR" ,"TSXV:MGM" ,"TSXV:MMG" ,"TSXV:BBB"
            ,"TSXV:PGZ" ,"TSXV:WEX" ,"TSXV:PHNM" ,"TSXV:KRI" ,"TSXV:AZS" ,"TSXV:NAU" ,"TSXV:KDK" ,"TSXV:CNO"
            ,"TSXV:AUM" ,"TSXV:MRZ" ,"TSXV:MD" ,"TSXV:JG" ,"TSXV:FRED" ,"TSXV:RPX" ,"TSXV:GUS" ,"TSXV:AGX"
            ,"TSXV:SURG" ,"TSXV:WHY" ,"TSXV:GLAD" ,"TSXV:EVNI" ,"TSXV:TTS" ,"TSXV:ARU" ,"TSXV:STS" ,"TSXV:WPG"
            ,"TSXV:GUN" ,"TSXV:PPX" ,"TSXV:ONAU" ,"TSXV:A.H" ,"TSXV:RGC" ,"TSXV:AGLD" ,"TSXV:JRV" ,"TSXV:SR"
            ,"TSXV:FCLI" ,"TSXV:RYR" ,"TSXV:EAM" ,"TSXV:FTZ" ,"TSXV:CTM" ,"TSXV:RDG" ,"TSXV:B" ,"TSXV:ABR"
            ,"TSXV:BFM" ,"TSXV:TRBC" ,"TSXV:TUK" ,"TSXV:CD" ,"TSXV:ROS" ,"TSXV:XXIX" ,"TSXV:AAN" ,"TSXV:GLDC"
            ,"TSXV:WHN" ,"TSXV:ELBM" ,"TSXV:YGT" ,"TSXV:SGO" ,"TSXV:UGD" ,"TSXV:MUN" ,"TSXV:EMM" ,"TSXV:KTO"
            ,"TSXV:DLTA" ,"TSXV:PEAK" ,"TSXV:GPG" ,"TSXV:EDG" ,"TSXV:TRO" ,"TSXV:AZT" ,"TSXV:SPMC" ,"TSXV:EMNT"
            ,"TSXV:FTUR" ,"TSXV:RAK" ,"TSXV:CPS" ,"TSXV:GG" ,"TSXV:KC" ,"TSXV:STE" ,"TSXV:FAN" ,"TSXV:FOR"
            ,"TSXV:KG" ,"TSXV:STRR" ,"TSXV:PJX" ,"TSXV:LVG" ,"TSXV:ALTA" ,"TSXV:RK" ,"TSXV:TECT" ,"TSXV:SRL"
            ,"TSXV:RSM" ,"TSXV:FEO" ,"TSXV:TK" ,"TSXV:BKM" ,"TSXV:KCP" ,"TSXV:FMAN" ,"TSXV:CVB" ,"TSXV:NPR"
            ,"TSXV:INTR" ,"TSXV:FNC" ,"TSXV:KNG" ,"TSXV:CMU" ,"TSXV:SLG" ,"TSXV:FKM" ,"TSXV:SMD" ,"TSXV:CN"
            ,"TSXV:COSA" ,"TSXV:EML" ,"TSXV:SRC" ,"TSXV:SOI" ,"TSXV:CNX" ,"TSXV:GGO" ,"TSXV:SBMI" ,"TSXV:TWR"
            ,"TSXV:HHH" ,"TSXV:VAU" ,"TSXV:IVS" ,"TSXV:PGDC" ,"TSXV:ROAR" ,"TSXV:ZNX" ,"TSXV:PLAN" ,"TSXV:AGMR"
            ,"TSXV:BEX" ,"TSXV:SME" ,"TSXV:BMR" ,"TSXV:WML" ,"TSXV:KFR" ,"TSXV:ELEC" ,"TSXV:THM" ,"TSXV:JZR"
            ,"TSXV:MN" ,"TSXV:OCI" ,"TSXV:CCE" ,"TSXV:PTX" ,"TSXV:ASTR" ,"TSXV:AEMC" ,"TSXV:PNTR" ,"TSXV:RSLV"
            ,"TSXV:DAU" ,"TSXV:EPL" ,"TSXV:GBU" ,"TSXV:RARE" ,"TSXV:CAF" ,"TSXV:DEX" ,"TSXV:FMC" ,"TSXV:PX"
            ,"TSXV:VCU" ,"TSXV:MON" ,"TSXV:BRAU" ,"TSXV:PUMA" ,"TSXV:LBNK" ,"TSXV:LG" ,"TSXV:SIC" ,"TSXV:AUAU"
            ,"TSXV:TSLV" ,"TSXV:VMXX" ,"TSXV:GPM" ,"TSXV:GMA" ,"TSXV:RDU" ,"TSXV:MCC" ,"TSXV:HOH" ,"TSXV:LAB"
            ,"TSXV:BAY" ,"TSXV:TGOL" ,"TSXV:HDRO" ,"TSXV:MTS" ,"TSXV:TM" ,"TSXV:RYE" ,"TSXV:DRY" ,"TSXV:BHS"
            ,"TSXV:SHL" ,"TSXV:RRI" ,"TSXV:NOB" ,"TSXV:AMY" ,"TSXV:OMM" ,"TSXV:LEGY" ,"TSXV:ROCK" ,"TSXV:HPM"
            ,"TSXV:SGU" ,"TSXV:ABA" ,"TSXV:BRO" ,"TSXV:OOR" ,"TSXV:TBK" ,"TSXV:CBG" ,"TSXV:AIR" ,"TSXV:SGN"
            ,"TSXV:SUI" ,"TSXV:TVI" ,"TSXV:NMI" ,"TSXV:SWA" ,"TSXV:VAND" ,"TSXV:WLF" ,"TSXV:WCU" ,"TSXV:KAPA"
            ,"TSXV:KBRA" ,"TSXV:COR" ,"TSXV:LIS" ,"TSXV:TNR" ,"TSXV:LMS" ,"TSXV:ONYX" ,"TSXV:BAU" ,"TSXV:LEAP"
            ,"TSXV:VUL" ,"TSXV:LBC" ,"TSXV:BARU" ,"TSXV:TIN" ,"TSXV:LLG" ,"TSXV:OTS.H" ,"TSXV:SAE" ,"TSXV:NWX"
            ,"TSXV:PGLD" ,"TSXV:OZ" ,"TSXV:LOD" ,"TSXV:FBF" ,"TSXV:KALO" ,"TSXV:GIGA" ,"TSXV:VIZ" ,"TSXV:XIM"
            ,"TSXV:BMET" ,"TSXV:PRG" ,"TSXV:FYL" ,"TSXV:ATY" ,"TSXV:JUGR" ,"TSXV:BONE" ,"TSXV:SCY" ,"TSXV:SNG"
            ,"TSXV:CCMI" ,"TSXV:SLMN" ,"TSXV:GMV" ,"TSXV:TCO" ,"TSXV:GAL" ,"TSXV:CPAU" ,"TSXV:ILI" ,"TSXV:GRG"
            ,"TSXV:TORQ" ,"TSXV:DYG" ,"TSXV:NZP" ,"TSXV:GRDM" ,"TSXV:KORE" ,"TSXV:MAX" ,"TSXV:FMS" ,"TSXV:ZAC"
            ,"TSXV:SEND" ,"TSXV:WRR" ,"TSXV:RUG" ,"TSXV:GDP" ,"TSXV:TIG" ,"TSXV:GT" ,"TSXV:SAG" ,"TSXV:GGI"
            ,"TSXV:CPER" ,"TSXV:PE" ,"TSXV:VERT" ,"TSXV:DGC" ,"TSXV:RAGE" ,"TSXV:SGC" ,"TSXV:SMN" ,"TSXV:FFOX"
            ,"TSXV:KCC" ,"TSXV:EOX" ,"TSXV:SIE" ,"TSXV:APN" ,"TSXV:AWX" ,"TSXV:GRAT" ,"TSXV:TMET" ,"TSXV:TAJ"
            ,"TSXV:NTX" ,"TSXV:GSTM" ,"TSXV:LMR" ,"TSXV:REVX" ,"TSXV:ADE" ,"TSXV:REC" ,"TSXV:SRA" ,"TSXV:HPY"
            ,"TSXV:RG" ,"TSXV:FRI" ,"TSXV:TG" ,"TSXV:SAGA" ,"TSXV:RSMX" ,"TSXV:SRQ" ,"TSXV:PA" ,"TSXV:NBY"
            ,"TSXV:ELY" ,"TSXV:OPHR" ,"TSXV:TUF" ,"TSXV:TSD" ,"TSXV:CH" ,"TSXV:RLYG" ,"TSXV:PPP" ,"TSXV:OPW"
            ,"TSXV:SKP" ,"TSXV:NTH" ,"TSXV:CLCO" ,"TSXV:CANX" ,"TSXV:NAM" ,"TSXV:TWO" ,"TSXV:USCU" ,"TSXV:GTC"
            ,"TSXV:MMS" ,"TSXV:MAC" ,"TSXV:ROX" ,"TSXV:SPC" ,"TSXV:PGC" ,"TSXV:XGC" ,"TSXV:CELL" ,"TSXV:MTX"
            ,"TSXV:MTT" ,"TSXV:CPI" ,"TSXV:PGX" ,"TSXV:KS" ,"TSXV:FG" ,"TSXV:VLX" ,"TSXV:ZON" ,"TSXV:SNAG"
            ,"TSXV:CAM" ,"TSXV:QMC" ,"TSXV:BTU" ,"TSXV:BAT" ,"TSXV:STU" ,"TSXV:EXG" ,"TSXV:ZAU" ,"TSXV:MAZ.H"
            ,"TSXV:HAR" ,"TSXV:GQ" ,"TSXV:VIPR" ,"TSXV:LOT" ,"TSXV:BCU" ,"TSXV:ADD" ,"TSXV:VCT" ,"TSXV:PLA"
            ,"TSXV:ZBNI" ,"TSXV:VMS" ,"TSXV:SCD" ,"TSXV:AUQ" ,"TSXV:REX" ,"TSXV:ACS" ,"TSXV:TES" ,"TSXV:RNCH"
            ,"TSXV:NAR" ,"TSXV:VG" ,"TSXV:GXX" ,"TSXV:OLV" ,"TSXV:NIO" ,"TSXV:LVX" ,"TSXV:Q" ,"TSXV:MUR"
            ,"TSXV:WBE" ,"TSXV:BVA" ,"TSXV:APX" ,"TSXV:HELI" ,"TSXV:FIN" ,"TSXV:KLDC" ,"TSXV:SKRR" ,"TSXV:WGLD"
            ,"TSXV:MARV" ,"TSXV:GLDN" ,"TSXV:GGL" ,"TSXV:REE" ,"TSXV:LORD" ,"TSXV:VZZ" ,"TSXV:EDM" ,"TSXV:GCOM"
            ,"TSXV:RUSH" ,"TSXV:GGM" ,"TSXV:NBLC" ,"TSXV:GSPR" ,"TSXV:SGZ" ,"TSXV:DFR" ,"TSXV:GCX" ,"TSXV:HAY"
            ,"TSXV:BGF" ,"TSXV:MEK" ,"TSXV:CGD" ,"TSXV:ASL" ,"TSXV:KIP" ,"TSXV:MASS" ,"TSXV:AWM" ,"TSXV:CYL"
            ,"TSXV:NRN" ,"TSXV:CASA" ,"TSXV:COCO" ,"TSXV:ILC" ,"TSXV:IZN" ,"TSXV:QGR" ,"TSXV:LMG" ,"TSXV:DEC"
            ,"TSXV:ENDR" ,"TSXV:BTT" ,"TSXV:CBI" ,"TSXV:CCB" ,"TSXV:ANTL" ,"TSXV:QPM" ,"TSXV:VTT" ,"TSXV:BOL"
            ,"TSXV:NUG" ,"TSXV:LEXI" ,"TSXV:GUG" ,"TSXV:CTV" ,"TSXV:MERG" ,"TSXV:GRI" ,"TSXV:PEMC" ,"TSXV:STUD"
            ,"TSXV:MNRG" ,"TSXV:RTH" ,"TSXV:BLUE" ,"TSXV:TORA" ,"TSXV:FAIR" ,"TSXV:SRI" ,"TSXV:BMK" ,"TSXV:BGD"
            ,"TSXV:GHR.H" ,"TSXV:TBLL" ,"TSXV:EDW.H" ,"TSXV:KES" ,"TSXV:TRU" ,"TSXV:RKR" ,"TSXV:CRI" ,"TSXV:FMN"
            ,"TSXV:NICN" ,"TSXV:PINN" ,"TSXV:KGC" ,"TSXV:GZD" ,"TSXV:OC" ,"TSXV:PAT" ,"TSXV:USHA" ,"TSXV:MKR"
            ,"TSXV:WSK" ,"TSXV:SAF" ,"TSXV:SPX" ,"TSXV:AERO" ,"TSXV:VLV" ,"TSXV:GMI" ,"TSXV:EVER" ,"TSXV:SWLF"
            ,"TSXV:AME" ,"TSXV:ABZ" ,"TSXV:IXI" ,"TSXV:AZR" ,"TSXV:ION" ,"TSXV:CRB" ,"TSXV:CMD" ,"TSXV:NCP"
            ,"TSXV:XTM" ,"TSXV:PHD" ,"TSXV:NIKL.H" ,"TSXV:GR" ,"TSXV:PLY" ,"TSXV:MCI" ,"TSXV:PERU" ,"TSXV:VGD"
            ,"TSXV:ZFR" ,"TSXV:ADZ" ,"TSXV:STNG" ,"TSXV:CQR" ,"TSXV:CPL" ,"TSXV:SXL" ,"TSXV:EGM" ,"TSXV:XND"
            ,"TSXV:MDM" ,"TSXV:VRR" ,"TSXV:PEX" ,"TSXV:AGT" ,"TSXV:NL" ,"TSXV:SDCU" ,"TSXV:SVG" ,"TSXV:BEA"
            ,"TSXV:DMI" ,"TSXV:PER" ,"TSXV:WINS" ,"TSXV:FAS" ,"TSXV:DOS" ,"TSXV:BM" ,"TSXV:DMCU" ,"TSXV:WGF"
            ,"TSXV:ERA" ,"TSXV:KTRI" ,"TSXV:RES" ,"TSXV:GRD" ,"TSXV:SMRV" ,"TSXV:RMO" ,"TSXV:IZZ" ,"TSXV:IMR"
            ,"TSXV:SILV" ,"TSXV:WKG" ,"TSXV:CIO" ,"TSXV:NEV" ,"TSXV:GEM" ,"TSXV:JADE" ,"TSXV:CUEX" ,"TSXV:WLR"
            ,"TSXV:AMZ" ,"TSXV:ICM" ,"TSXV:EFF" ,"TSXV:SAO" ,"TSXV:GLD" ,"TSXV:PEGA" ,"TSXV:MT" ,"TSXV:TRS"
            ,"TSXV:SPD" ,"TSXV:RYO" ,"TSXV:TR" ,"TSXV:HMAN" ,"TSXV:EMR" ,"TSXV:BMV" ,"TSXV:ESPN" ,"TSXV:MLO"
            ,"TSXV:IMC" ,"TSXV:CUCO.H" ,"TSXV:STRM" ,"TSXV:EDDY" ,"TSXV:GDX" ,"TSXV:ZCC.H" ,"TSXV:WAV.H" ,"TSXV:GEL"
            ,"TSXV:MTB" ,"TSXV:KTR" ,"TSXV:ULT" ,"TSXV:HVG" ,"TSXV:AWE" ,"TSXV:TAB.H" ,"TSXV:RJX.A" ,"TSXV:ORS"
            ,"TSXV:ETF" ,"TSXV:CLM" ,"TSXV:ACDC" ,"TSXV:CBA" ,"TSXV:BLDS" ,"TSXV:NVX" ,"TSXV:ALM" ,"TSXV:JDN"
            ,"TSXV:POR" ,"TSXV:PBM" ,"TSXV:BGS" ,"TSXV:ODX.H" ,"TSXV:SMP" ,"TSXV:FMM" ,"TSXV:GSS" ,"TSXV:AUEN"
            ,"TSXV:RBZ" ,"TSXV:TRAN" ,"TSXV:RTM" ,"TSXV:WMS" ,"TSXV:SKYG" ,"TSXV:GGX" ,"TSXV:SSE" ,"TSXV:NNX"
            ,"TSXV:PRS.H" ,"TSXV:HANS" ,"TSXV:LPK" ,"TSXV:ICON" ,"TSXV:QCX" ,"TSXV:MEX" ,"TSXV:ABM" ,"TSXV:RTE"
            ,"TSXV:CML" ,"TSXV:CCD" ,"TSXV:SCLT" ,"TSXV:ALT" ,"TSXV:CLV" ,"TSXV:FTJ" ,"TSXV:GOFL" ,"TSXV:DG"
            ,"TSXV:IRI" ,"TSXV:BAG" ,"TSXV:GCN" ,"TSXV:GAB" ,"TSXV:DCOP" ,"TSXV:AML" ,"TSXV:STA" ,"TSXV:CMIL"
            ,"TSXV:RMI" ,"TSXV:NORR" ,"TSXV:KNC" ,"TSXV:CZZ" ,"TSXV:SMR.H" ,"TSXV:MTH" ,"TSXV:MQM" ,"TSXV:VRB"
            ,"TSXV:TEA" ,"TSXV:TFM" ,"TSXV:CRD" ,"TSXV:MHI" ,"TSXV:GNG" ,"TSXV:DTWO" ,"TSXV:TKU" ,"TSXV:MMN"
            ,"TSXV:ENRG" ,"TSXV:KGS" ,"TSXV:EGR" ,"TSXV:CCDS" ,"TSXV:BIGT" ,"TSXV:GBML" ,"TSXV:AVG" ,"TSXV:CBLT"
            ,"TSXV:ETU" ,"TSXV:CUCU" ,"TSXV:ATOM" ,"TSXV:MSC" ,"TSXV:GHL" ,"TSXV:BRL.H" ,"TSXV:APMI" ,"TSXV:ROVR"
            ,"TSXV:MINE" ,"TSXV:CLUS" ,"TSXV:BWR" ,"TSXV:COS" ,"TSXV:EAU" ,"TSXV:VCV" ,"TSXV:BRON" ,"TSXV:CEO.H"
            ,"TSXV:AIS" ,"TSXV:INFM" ,"TSXV:INFD" ,"TSXV:GEMC" ,"TSXV:BOCA" ,"TSXV:LKY" ,"TSXV:KGL.H" ,"TSXV:EPO.H"
            ,"TSXV:DHR" ,"TSXV:INFI" ,"TSXV:AVU" ,"TSXV:TNO.H" ,"TSXV:RCT" ,"TSXV:DCY" ,"TSXV:GVR" ,"TSXV:FUSE"
            ,"TSXV:CONE" ,"TSXV:RMD" ,"TSXV:QURI" ,"TSXV:NICK" ,"TSXV:STUV" ,"TSXV:TORC" ,"TSXV:VKG.H" ,"TSXV:CTG"
            ,"TSXV:KLM" ,"TSXV:FEX" ,"TSXV:LONE" ,"TSXV:ZLTO" ,"TSXV:CZ.H" ,"TSXV:SYG" ,"TSXV:AUR.H" ,"TSXV:NVT"
            ,"TSXV:NED" ,"TSXV:XPLR" ,"TSXV:GFM.H" ,"TSXV:BNZ" ,"TSXV:CAND" ,"TSXV:OWN" ,"TSXV:GER" ,"TSXV:BAL.H"
            ,"TSXV:NEW.H" ,"TSXV:QRO" ,"TSXV:IMM" ,"TSXV:OTGO.H" ,"TSXV:LSTR" ,"TSXV:ARJN" ,"TSXV:BST" ,"TSXV:CTN"
            ,"TSXV:MTN.H" ,"TSXV:GLI" ,"TSXV:WRI" ,"TSXV:ACR.H" ,"TSXV:EON" ,"TSXV:AORO" ,"TSXV:GGG.H" ,"TSXV:AGH.H"
            ,"TSXV:GIG" ,"TSXV:BIRD" ,"TSXV:AUGC" ,"TSXV:NZN" ,"TSXV:LWR" ,"TSXV:NER.H" ,"TSXV:AFR" ,"TSXV:NXS"
            ,"TSXV:PEC.H" ,"TSXV:GAR.H" ,"TSXV:CSL.H" ,"TSXV:HAWK" ,"TSXV:KIB" ,"TSXV:GETT" ,"TSXV:IZ" ,"TSXV:IDI.H"
            ,"TSXV:PPM" ,"TSXV:VAX" ,"TSXV:NTB" ,"TSXV:HBK.H" ,"TSXV:MGI" ,"TSXV:MCA.H" ,"TSXV:CMO.H" ,"TSXV:KEN.H"
            ,"TSXV:AVX" ,"TSXV:PGP" ,"TSXV:MTK" ,"TSXV:WR.H" ,"TSXV:OPTG" ,"TSXV:CFA.H"};

    public static void main(String[] args) {
        List<List<String>> records = new ArrayList<>();
        File f;

        if (args.length < 1) {
            System.out.println("Error input file name(s) must be specified\n\n");
            return;
        }

        for (int i=0; i<args.length; i++) {    // read all files
            String fileName = args[i];

            f = new File(fileName);
            if (f.isDirectory()) {
                System.out.println("Error - file (" + fileName + ") is a directory, please specify a CSV file as input\n\n");
                return;
            }

            if (f.exists()) {
                records = readCsvFile(fileName, records);
            } else {
                System.out.println("Error - file (" + fileName + ") does not exist, please specify a valid CSV file as input\n\n");
            }
        }

        outputSelectedMiningData (records);
    }

    private static List<List<String>> readCsvFile(String fileName, List<List<String>> records) {
        System.out.println ("Processing file - " + fileName + "\n");
        headingFlag = false;
        int[] swapOrder;
        int count = 0;
        int fileType;

        if (fileName.contains(".tsv")) {
            fileType = TAB;
        } else {
            fileType = COMMA;
        }

        swapOrder = initialiseSwapOrder ();
        try (
                BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (headingFlag == false) {
                        if (fileType == COMMA) {
                            headingLine = line.split(COMMA_DELIMITER);
                        } else {
                            //String outputLine = String.format( "Heading line 1 (%s)", line);
                            //System.out.println(outputLine);

                            String nextLine = br.readLine();

                            //String outputLine2 = String.format( "Heading line 2 (%s)", nextLine);
                            //System.out.println(outputLine2);

                            if (line.charAt(line.length() - 1) != '\t' &&
                                nextLine.charAt(0) != '\t') {   // add a tab if seperator missing between lines
                                line = line.concat ("\t");
                            }
                            line = line.concat (nextLine);
                            headingLine = line.split(TAB_DELIMITER);
                            //logHeadingLine (headingLine);
                        }
                        swapOrder = getSwapOrder (headingLine, swapOrder);
                        //logSwappOrder (swapOrder);
                        headingFlag = true;
                    } else {
                        String[] swappedValues;
                        String[] values;

                        if (fileType == COMMA) {
                            values = line.split(COMMA_DELIMITER);
                        } else {
                            values = line.split(TAB_DELIMITER);
                        }
                        swappedValues = swapValues(values, swapOrder);

                        if (fileType == TAB) {
                            //logSwappedValues (swappedValues);
                            swappedValues = correctStockTicker(swappedValues);
                        }
                        //logSwappedValues (swappedValues);
                        records.add(Arrays.asList(swappedValues));
                        count++;
                    }
                }
        } catch (FileNotFoundException e) {
            System.out.println("Error - file (" + fileName + ") does not exist, please specify a valid CSV file as input\n\n");
            exit(1);
        }
        catch (IOException e) {
            System.out.println("Error - while attempting to read from file, error = (" + e + ") \n\n");
            exit(2);
        }

        System.out.println ("Lines read = " + count + "\n");
        return records;
    }

    // Data grabbed directly from the StockAnalysis.com website (TSV) has a different Ticker synbol,
    // not prefixed with the stock exchange it belongs to, this needs to be corrected.
    private static String[] correctStockTicker (String[] values) {
        String symbol = values[SYMBOL];
        String exchange = values[EXCHANGE];

        String exchangeTicker = "UNKNOWN";
        for (int i=0; i < exchangeTickerRelation.length; i++) {
            if (exchange.equals (exchangeTickerRelation[i][EXCHANGE_FULL_NAME])) {
                exchangeTicker = exchangeTickerRelation[i][EXCHANGE_TICKER];
            }
        }
        if (exchangeTicker.equals ("UNKNOWN")) {
            String outputLine = String.format("Problem encountered, Unknown Exchange encountered, exchange = (%s)", exchange);
            System.out.println(outputLine);
        } else {
            String newStockTicker = exchangeTicker.concat(":").concat(symbol);
            values[SYMBOL] = newStockTicker;

            //String outputLine = String.format("newStockTicker = (%s)", newStockTicker);
            //System.out.println(outputLine);
        }

        return values;
    }

    private static void logHeadingLine (String[] headingLine) {
        String outputLine = new String();

        outputLine = String.format( "Heading line parameters = ");
        for (int i = 0; i < outputCsvOrder.length; i++) {
            String param = String.format( "(%s),", headingLine[i]);
            outputLine = outputLine.concat(param);
        }
        System.out.println(outputLine);
    }

    private static void logSwappOrder (int[] swapOrder) {
        String outputLine = new String();

        outputLine = String.format( "Swap Order = ");

        for (int i = 0; i < outputCsvOrder.length; i++) {
            String param = String.format( "%d-%d,", i, swapOrder[i]);
            outputLine = outputLine.concat(param);
        }
        System.out.println(outputLine);
    }

    private static void logSwappedValues (String[] swappedValues) {
        String outputLine = new String();
        outputLine = String.format( "Swapped values = ");

        for (int i=0; i<outputCsvOrder.length; i++) {
            String param = String.format( "(%s),", swappedValues[i]);
            outputLine = outputLine.concat(param);
        }
        System.out.println(outputLine);
    }

    // New heading line encountered, initialise the swap order then process the headings
    // to work out how to swap the paremeters anound to keep them consistent
    private static int[] initialiseSwapOrder () {
        int[] swapOrder = new int[MAX_PARAMETERS];

        for (int i=0; i<MAX_PARAMETERS; i++) {
            swapOrder[i] = -1;
        }
        return swapOrder;
    }

    // re-order the columns into the expected order for output.
    private static String[] swapValues (String[] values, int[] swapOrder) {
        String[] swappedValues = new String[MAX_PARAMETERS];

        for (int i=0; i<outputCsvOrder.length; i++) {
            if (swapOrder[i] != -1) {
                //outputLine = String.format( "swapping param=(%d), set to (%s)", i, values[swapOrder[i]]);
                //System.out.println(outputLine);

                String newValue = values[getSwapColumn(i, swapOrder)];
                if (newValue.equals ("-")) {
                    swappedValues[i] = "";
                } else {
                    swappedValues[i] = values[getSwapColumn(i, swapOrder)];
                    swappedValues[i] = standardiseNumbers (swappedValues[i]);
                }
            } else {
                swappedValues[i] = "";
            }
        }

        return swappedValues;
    }

    // if number is displayed in K-thoussand or M-Million or B-Billion, convert to the full number
    private static String standardiseNumbers (String field) {
        field = field.replace(",", "");
        String standardNumber = new String();

        if (Pattern.matches("-?\\d+(\\.\\d+)[BKMT]?", field)) {
            Double convertedNumber;
            char multiplier = field.charAt(field.length() - 1);

            standardNumber = field.substring(0, field.length() - 1);
            convertedNumber = Double.parseDouble(standardNumber);
            char multiplierUpper = Character.toUpperCase(multiplier);
            switch (multiplierUpper) {
                case 'K':
                    convertedNumber = convertedNumber * 1000;
                    break;
                case 'M':
                    convertedNumber = convertedNumber * 1000000;
                    break;
                case 'B':
                    convertedNumber = convertedNumber * 1000000000;
                    break;
                case 'T':
                    convertedNumber = convertedNumber * 1000000000000L;
                    break;
                default:
                    // Assume no suffix found so return the original number
                    return field;
            }
            standardNumber = String.format("%.8f", convertedNumber);
            standardNumber = stripTrailingSpaces (standardNumber);

            //String outputLine = String.format( "param found that needs conversion =(%s) becomes (%s)", field, standardNumber);
            //System.out.println(outputLine);
        } else {
            return field;
        }

        return standardNumber;

    }

    // remove trailing spaces from floating point number and decimal point if needed
    private static String stripTrailingSpaces (String standardNumber) {
        StringBuilder str = new StringBuilder(standardNumber);

        //String outputLine = String.format( "removing trailing spaces from (%s)", standardNumber);
        //System.out.println(outputLine);

        for (int i = str.length()-1; i>0; i--) {
            //outputLine = String.format( "str.charAt(%d) == (%c)", i, str.charAt(i));
            //System.out.println(outputLine);
            if (str.charAt(i) == '0') {
                //outputLine = String.format( "Setting char (%d) to null", i);
                //System.out.println(outputLine);
                //str = str.substring (0, i-1);
                //str.setCharAt(i, '\0');
            } else {
                if (str.charAt(i) == '.') {
                    //outputLine = String.format( "Setting decimal point char (%d) to null", i);
                    //System.out.println(outputLine);
                    //str = str.substring (0, i-1);
                    //i--;
                    //str.setCharAt(i, '\0');
                }
                //outputLine = String.format( "returning string (%s)", str.toString());
                //System.out.println(outputLine);
                //str = str.substring (0, i);
                standardNumber = standardNumber.substring (0, i);
                return standardNumber; //.toString();
            }
        }
        //outputLine = String.format( "out of loop - returning string (%s)", str.toString());
        //System.out.println(outputLine);
        return  str.toString();
    }

    private static int getSwapColumn (int index, int[] swapOrder) {
        for (int i=0; i<outputCsvOrder.length; i++) {
            if (swapOrder[i] == index) {
                return i;
            }
        }
        return 0;
    }

    // from the headings work out the swap order so data is output in a standard order
    // as different input files might have their columns in a different order.
    private static int[] getSwapOrder (String[] headingLine, int[] swapOrder) {

        //System.out.println("Visual comparison\n");
        //for (int headIdx=0; headIdx<outputCsvOrder.length; headIdx++) {
        //    String outputLine = String.format( "Heading (%s) output (%s),", headingLine[headIdx], outputCsvOrder[headIdx]);
        //    System.out.println(outputLine);
        //}

        for (int headIdx=0; headIdx<headingLine.length; headIdx++) {
            swapOrder[headIdx] = -1;
            for (int i=0; i<outputCsvOrder.length; i++) {
                if (headingLine[headIdx].trim().equals (outputCsvOrder[i].trim())) {
                    swapOrder[headIdx] = i;

                    //String outputLine = String.format( "Heading idx %d Matches output idx %d - for heading (%s),", headIdx, i, headingLine[headIdx]);
                    //System.out.println(outputLine);

                    i=headingLine.length;
                }
            }
        }
        return swapOrder;
    }

    private static void outputSelectedMiningData (List<List<String>> records) {
        String initialLine = commaSeperateLine (outputCsvOrder);
        int row_number = 1;
        FileWriter wr;

        try {
            File path = new File(MINING_DATA_FILE_NAME);
            wr = new FileWriter(path);
        } catch (IOException ex) {
            System.out.print("Error attempting to create output file " + MINING_DATA_FILE_NAME + "\n\n");
            return;
        }

        writeOutputLine (initialLine, MINING_DATA_FILE_NAME, wr);
        for (String currentSymbol : miningCompanySymbols) {
            String outputLine;

            boolean found=false;
            for (List<String> line : records) {
                String compStr = line.get(1);

                if (currentSymbol.equals(compStr)) {
                    outputLine = commaSeperateLine(line);
                    writeOutputLine (outputLine, MINING_DATA_FILE_NAME, wr);
                    found=true;
                    break;
                }
            }
            if (found == false) {
                outputLine = "" + row_number + "," + currentSymbol + ",,,,,,,,,,,,,,,,,,,,,,,,,,,,\n";
                writeOutputLine (outputLine, MINING_DATA_FILE_NAME, wr);
            }
        }

        try {
            wr.flush();
            wr.close();
        }  catch (IOException ex) {
            System.out.print("Error attempting to flush and close output file " + MINING_DATA_FILE_NAME + "\n\n");
        }
    }

    private static void writeOutputLine (String line, String fileName, FileWriter wr) {
        try {
            //calling writer.write() method with the string
            wr.write(line);
        }
        catch (IOException ex) {
            System.out.print("Error attempting to write to output file " + fileName + "\n\n");
        }
    }

    private static String commaSeperateLine (String[] line) {
        String commaSepLine = new String();

        for (int i = 0; i < outputCsvOrder.length; i++) {
            if (i == 0) {
                commaSepLine = line[i];
            } else {
                commaSepLine = commaSepLine.concat(line[i]);
            }
            if (i<line.length-1) { commaSepLine = commaSepLine.concat(","); }
        }
        commaSepLine = commaSepLine.concat("\n");
        return (commaSepLine);
    }

    private static String commaSeperateLine (List <String> line) {
        String commaSepLine = new String();

        for (int i = 0; i < outputCsvOrder.length; i++) {
            if (i == 0) {
                commaSepLine = line.get(i);
            } else {
                commaSepLine = commaSepLine.concat(line.get(i));
            }
            if (i<line.size()-1) { commaSepLine = commaSepLine.concat(","); }
        }
        commaSepLine = commaSepLine.concat("\n");

        return (commaSepLine);
    }
}