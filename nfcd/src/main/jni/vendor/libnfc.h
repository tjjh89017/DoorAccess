

/*
*****************************************************************
* data structures extracted from the original libnfc sourcecode *
*****************************************************************
*/



#define UINT16_TO_BE_STREAM(p, u16) {*(p)++ = (uint8_t)((u16) >> 8); *(p)++ = (uint8_t)(u16);}
#define UINT8_TO_BE_STREAM(p, u8)   {*(p)++ = (uint8_t)(u8);}
#define BE_STREAM_TO_UINT8(u8, p)   {u8 = (uint8_t)(*(p)); (p) += 1;}
#define BE_STREAM_TO_UINT16(u16, p) {u16 = (uint16_t)(((uint16_t)(*(p)) << 8) + (uint16_t)(*((p) + 1))); (p) += 2;}

typedef void (tCE_CBACK) (uint8_t event, void *p_data);
typedef uint8_t CE_T4tRegisterAID (uint8_t aid_len, uint8_t *p_aid, tCE_CBACK  *p_cback);



typedef struct
{
    uint16_t          event;
    uint16_t          len;
    uint16_t          offset;
    uint16_t          layer_specific;
} BT_HDR;
typedef struct {
    uint8_t             status;         /* The event status                 */
    BT_HDR                  *p_data;        /* The received Data                */
} tNFC_DATA_CEVT;
typedef union {
    tNFC_DATA_CEVT          data;
} tNFC_CONN;
typedef struct
{
    uint8_t     status;
    uint8_t          *p_data;
    uint8_t         b_updated;
    uint32_t          length;
} tCE_UPDATE_INFO;
typedef struct
{
    uint8_t     status;
    uint8_t           aid_handle;
    BT_HDR         *p_data;
} tCE_RAW_FRAME;
typedef union
{
    uint8_t         status;
    tCE_UPDATE_INFO     update_info;
    tCE_RAW_FRAME       raw_frame;
} tCE_DATA;
#define CE_T3T_FIRST_EVT    0x60
#define CE_T4T_FIRST_EVT    0x80
enum
{
    CE_T3T_NDEF_UPDATE_START_EVT = CE_T3T_FIRST_EVT,
    CE_T3T_NDEF_UPDATE_CPLT_EVT,
    CE_T3T_UPDATE_EVT,
    CE_T3T_CHECK_EVT,
    CE_T3T_RAW_FRAME_EVT,
    CE_T3T_MAX_EVT,

    CE_T4T_NDEF_UPDATE_START_EVT  = CE_T4T_FIRST_EVT,
    CE_T4T_NDEF_UPDATE_CPLT_EVT,
    CE_T4T_NDEF_UPDATE_ABORT_EVT,
    CE_T4T_RAW_FRAME_EVT,
    CE_T4T_MAX_EVT
};

/***************************************************/
typedef void (tNFC_CONN_CBACK) (uint8_t conn_id, uint16_t event, tNFC_CONN *p_data);
typedef void NFC_SetStaticRfCback (tNFC_CONN_CBACK *p_cback);

typedef unsigned char   UINT8;
typedef unsigned short  UINT16;
typedef unsigned char   BOOLEAN;
typedef BOOLEAN nfc_hal_nci_receive_msg (UINT8 byte);
typedef UINT8 tUSERIAL_PORT;

typedef UINT8 tNFC_DISCOVERY_TYPE;

#define NCI_NFCID1_MAX_LEN    10
#define NCI_T1T_HR_LEN        2

typedef struct
{
    UINT8       sens_res[2];/* SENS_RES Response (ATQA). Available after Technology Detection */
    UINT8       nfcid1_len;         /* 4, 7 or 10 */
    UINT8       nfcid1[NCI_NFCID1_MAX_LEN]; /* AKA NFCID1 */
    UINT8       sel_rsp;    /* SEL_RSP (SAK) Available after Collision Resolution */
    UINT8       hr_len;     /* 2, if T1T HR0/HR1 is reported */
    UINT8       hr[NCI_T1T_HR_LEN]; /* T1T HR0 is in hr[0], HR1 is in hr[1] */
} tNCI_RF_PA_PARAMS;

typedef tNCI_RF_PA_PARAMS tNFC_RF_PA_PARAMS;

#define NFC_MAX_SENSB_RES_LEN 12
#define NFC_NFCID0_MAX_LEN          4

typedef struct
{
    UINT8       sensb_res_len;/* Length of SENSB_RES Response (Byte 2 - Byte 12 or 13) Available after Technology Detection */
    UINT8       sensb_res[NFC_MAX_SENSB_RES_LEN]; /* SENSB_RES Response (ATQ) */
    UINT8       nfcid0[NFC_NFCID0_MAX_LEN];
} tNFC_RF_PB_PARAMS;

#define NFC_MAX_SENSF_RES_LEN 18
#define NFC_NFCID2_LEN 8
#define NCI_NFCID2_LEN 8

typedef struct
{
    UINT8       bit_rate;/* NFC_BIT_RATE_212 or NFC_BIT_RATE_424 */
    UINT8       sensf_res_len;/* Length of SENSF_RES Response (Byte 2 - Byte 17 or 19) Available after Technology Detection */
    UINT8       sensf_res[NFC_MAX_SENSF_RES_LEN]; /* SENSB_RES Response */
    UINT8       nfcid2[NFC_NFCID2_LEN];  /* NFCID2 generated by the Local NFCC for NFC-DEP Protocol.Available for Frame Interface  */
    UINT8       mrti_check;
    UINT8       mrti_update;
} tNFC_RF_PF_PARAMS;

typedef struct
{
    UINT8       nfcid2[NCI_NFCID2_LEN];  /* NFCID2 generated by the Local NFCC for NFC-DEP Protocol.Available for Frame Interface  */
} tNCI_RF_LF_PARAMS;

typedef tNCI_RF_LF_PARAMS tNFC_RF_LF_PARAMS;

#define NFC_ISO15693_UID_LEN        8
typedef struct
{
    UINT8       flag;
    UINT8       dsfid;
    UINT8       uid[NFC_ISO15693_UID_LEN];
} tNFC_RF_PISO15693_PARAMS;

#ifndef NFC_KOVIO_MAX_LEN
#define NFC_KOVIO_MAX_LEN       32
#endif
typedef struct
{
    UINT8       uid_len;
    UINT8       uid[NFC_KOVIO_MAX_LEN];
} tNFC_RF_PKOVIO_PARAMS;

typedef union
{
    tNFC_RF_PA_PARAMS   pa;
    tNFC_RF_PB_PARAMS   pb;
    tNFC_RF_PF_PARAMS   pf;
    tNFC_RF_LF_PARAMS   lf;
    tNFC_RF_PISO15693_PARAMS pi93;
    tNFC_RF_PKOVIO_PARAMS pk;
} tNFC_RF_TECH_PARAMU;

typedef struct
{
    tNFC_DISCOVERY_TYPE     mode;
    tNFC_RF_TECH_PARAMU     param;
} tNFC_RF_TECH_PARAMS;

typedef void nfa_ce_init (void);

typedef UINT8 NFA_SetTraceLevel (UINT8 new_level);

typedef UINT8 tNFA_STATUS;
typedef UINT8 tNFC_STATUS;
typedef UINT8 tNFA_PROTOCOL_MASK;
typedef unsigned long   UINT32;
typedef UINT32  tNFA_DM_DISC_TECH_PROTO_MASK;
typedef void HAL_NfcWrite (UINT16 data_len, UINT8 *p_data);
typedef UINT16  USERIAL_Write(tUSERIAL_PORT port, UINT8 *p_data, UINT16 len);
typedef tNFA_STATUS nfa_dm_set_rf_listen_mode_config (tNFA_DM_DISC_TECH_PROTO_MASK tech_proto_mask);
typedef tNFC_STATUS NFC_SetConfig (UINT8     tlv_size, UINT8    *p_param_tlvs);

/***************************************************/

#define T3T_MSG_SERVICE_LIST_MAX                    16
#define NCI_NFCID2_LEN              8
#define NCI_T3T_PMM_LEN             8
#define NCI_RF_F_UID_LEN            NCI_NFCID2_LEN
#define NCI_MAX_AID_LEN     16
#define NFC_MAX_AID_LEN     NCI_MAX_AID_LEN     /* 16 */
#define CE_T4T_MAX_REG_AID         4
#define T4T_FC_TLV_OFFSET_IN_CC           0x07
#define T4T_FILE_CONTROL_TLV_SIZE       0x08
typedef struct _tle
{
    struct _tle  *p_next;
    struct _tle  *p_prev;
    void  *p_cback;
    uint32_t         ticks;
    uint32_t   param;
    uint16_t        event;
    uint8_t         in_use;
} TIMER_LIST_ENT;

typedef struct {
    unsigned char         initialized;
    uint8_t           version;        /* Ver: peer version */
    uint8_t           nbr;            /* NBr: number of blocks that can be read using one Check command */
    uint8_t           nbw;            /* Nbw: number of blocks that can be written using one Update command */
    uint16_t          nmaxb;          /* Nmaxb: maximum number of blocks available for NDEF data */
    uint8_t           writef;         /* WriteFlag: 00h if writing data finished; 0Fh if writing data in progress */
    uint8_t           rwflag;         /* RWFlag: 00h NDEF is read-only; 01h if read/write available */
    uint32_t          ln;
    uint8_t           *p_buf;         /* Current contents for READs */

    /* Scratch NDEF buffer (for update NDEF commands) */
    uint8_t           scratch_writef;
    uint32_t          scratch_ln;
    uint8_t           *p_scratch_buf; /* Scratch buffer for WRITE/readback */
} tCE_T3T_NDEF_INFO;
typedef struct {
    uint16_t          service_code_list[T3T_MSG_SERVICE_LIST_MAX];
    uint8_t           *p_block_list_start;
    uint8_t           *p_block_data_start;
    uint8_t           num_services;
    uint8_t           num_blocks;
} tCE_T3T_CUR_CMD;
typedef struct
{
    uint8_t               state;
    uint16_t              system_code;
    uint8_t               local_nfcid2[NCI_RF_F_UID_LEN];
    uint8_t               local_pmm[NCI_T3T_PMM_LEN];
    tCE_T3T_NDEF_INFO   ndef_info;
    tCE_T3T_CUR_CMD     cur_cmd;
} tCE_T3T_MEM;
typedef struct
{
    uint8_t               aid_len;
    uint8_t               aid[NFC_MAX_AID_LEN];
    tCE_CBACK          *p_cback;
} tCE_T4T_REG_AID;      /* registered AID table */

typedef struct
{
    TIMER_LIST_ENT      timer;              /* timeout for update file              */
    uint8_t               cc_file[T4T_FC_TLV_OFFSET_IN_CC + T4T_FILE_CONTROL_TLV_SIZE];
    uint8_t              *p_ndef_msg;         /* storage of NDEF message              */
    uint16_t              nlen;               /* current size of NDEF message         */
    uint16_t              max_file_size;      /* size of storage + 2 bytes for NLEN   */
    uint8_t              *p_scratch_buf;      /* temp storage of NDEF message for update */

#define CE_T4T_STATUS_T4T_APP_SELECTED      0x01    /* T4T CE App is selected       */
#define CE_T4T_STATUS_REG_AID_SELECTED      0x02    /* Registered AID is selected   */
#define CE_T4T_STATUS_CC_FILE_SELECTED      0x04    /* CC file is selected          */
#define CE_T4T_STATUS_NDEF_SELECTED         0x08    /* NDEF file is selected        */
#define CE_T4T_STATUS_NDEF_FILE_READ_ONLY   0x10    /* NDEF is read-only            */
#define CE_T4T_STATUS_NDEF_FILE_UPDATING    0x20    /* NDEF is updating             */
#define CE_T4T_STATUS_WILDCARD_AID_SELECTED 0x40    /* Wildcard AID selected        */

    uint8_t               status;

    tCE_CBACK          *p_wildcard_aid_cback;               /* registered wildcard AID callback */
    tCE_T4T_REG_AID     reg_aid[CE_T4T_MAX_REG_AID];        /* registered AID table             */
    uint8_t               selected_aid_idx;
} tCE_T4T_MEM;
typedef struct
{
    tCE_T3T_MEM         t3t;
    tCE_T4T_MEM         t4t;
} tCE_MEM;
typedef struct
{
    tCE_MEM             mem;
    tCE_CBACK           *p_cback;
    uint8_t               *p_ndef;     /* the memory starting from NDEF */
    uint16_t              ndef_max;    /* max size of p_ndef */
    uint16_t              ndef_cur;    /* current size of p_ndef */
    uint8_t        tech;
    uint8_t               trace_level;

} tCE_CB;
