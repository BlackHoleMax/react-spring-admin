export interface Result<T = unknown> {
  code: number;
  msg: string;
  data: T;
  timestamp: number;
  path: string;
  page?: PageInfo;
}

export interface PageInfo {
  total: number;
  size: number;
  current: number;
}

export interface PageResult<T = unknown> {
  size: number;
  current: number;
  total: number;
  records: T[];
  pages: number;
}

export interface User {
  id: number;
  username: string;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  status: number;
  delFlag?: number;
  createTime?: string;
  updateTime?: string;
}

export interface UserProfile {
  id: number;
  username: string;
  nickname: string;
  avatar: string;
  email: string;
  phone: string;
  gender?: number;
  createTime: string;
  loginCount: number;
}

export interface UpdatePasswordParams {
  oldPassword: string;
  newPassword: string;
}

export interface UpdateProfileParams {
  nickname: string;
  email: string;
  phone: string;
  gender?: number;
}

export interface Role {
  id: number;
  name: string;
  code: string;
  sort?: number;
  status: number;
  delFlag?: number;
  createTime?: string;
  updateTime?: string;
}

export interface Menu {
  id: number;
  parentId: number;
  name: string;
  path: string;
  component?: string;
  redirect?: string;
  icon?: string;
  sort?: number;
  hidden?: number;
  external?: number;
  perms?: string;
  status: number;
  delFlag?: number;
  createTime?: string;
  updateTime?: string;
  children?: Menu[];
}

export interface Permission {
  id: number;
  perm: string;
  name: string;
  menuId?: number;
  menuName?: string;
}

export interface Dict {
  id: number;
  dictName: string;
  dictCode: string;
  sort?: number;
  status: number;
  remark?: string;
  delFlag?: number;
  createTime?: string;
  updateTime?: string;
}

export interface DictItem {
  id: number;
  dictId: number;
  label: string;
  value: string;
  sort?: number;
  status: number;
  remark?: string;
  delFlag?: number;
  createTime?: string;
  updateTime?: string;
}

export interface LoginLog {
  id: number;
  userId: number;
  username: string;
  ip: string;
  userAgent: string;
  status: number;
  msg: string;
  loginTime: string;
}

export interface OperLog {
  id: number;
  title: string;
  businessType: number;
  method: string;
  requestMethod: string;
  operatorType: number;
  operName: string;
  operUrl: string;
  operIp: string;
  operParam: string;
  jsonResult: string;
  status: number;
  errorMsg: string;
  operTime: string;
  costTime: number;
}

export type LoginParams = {
  username: string;
  password: string;
  remember?: boolean;
  captchaVerification?: string;
};

export type LoginResult = {
  token: string;
  userId: number;
  username: string;
  nickname?: string;
  permissions: string[];
  rememberMeToken?: string;
};

export interface UserQuery {
  current?: number;
  size?: number;
  username?: string;
  nickname?: string;
  status?: number;
}

export interface RoleQuery {
  current?: number;
  size?: number;
  name?: string;
  code?: string;
  status?: number;
}

export interface PermissionQuery {
  current?: number;
  size?: number;
  perm?: string;
  name?: string;
}

export interface MenuQuery {
  name?: string;
  status?: number;
}

export interface DictQuery {
  current?: number;
  size?: number;
  dictName?: string;
  dictCode?: string;
  status?: number;
}

export interface LoginLogQuery {
  current?: number;
  size?: number;
  username?: string;
  ip?: string;
  status?: number;
}

export interface OperLogQuery {
  current?: number;
  size?: number;
  title?: string;
  businessType?: number;
  status?: number;
  startTime?: string;
  endTime?: string;
}

export interface MonitorHealth {
  status: string;
  components: Record<
    string,
    {
      status: string;
      details?: Record<string, unknown>;
    }
  >;
}

export interface MonitorMetrics {
  'jvm.memory.used': number;
  'jvm.memory.max': number;
  'jvm.threads.live': number;
  'jvm.threads.peak': number;
  'process.cpu.usage': number;
  'system.cpu.usage': number;
  'http.server.requests.count': number;
  'heap.used': number;
  'heap.max': number;
  'heap.committed': number;
  'non-heap.used': number;
  uptime: number;
  'start.time': number;
  'available.processors': number;
  'system.load.average': number;
}

export interface JvmInfo {
  name: string;
  vendor: string;
  version: string;
  uptime: number;
  startTime: number;
  heapMemory: {
    init: number;
    used: number;
    committed: number;
    max: number;
  };
  nonHeapMemory: {
    init: number;
    used: number;
    committed: number;
    max: number;
  };
}

export interface SystemInfo {
  name: string;
  arch: string;
  version: string;
  availableProcessors: number;
  systemLoadAverage: number;
  totalMemory: number;
  freeMemory: number;
  maxMemory: number;
}

// 天爱验证码相关类型定义
export interface CaptchaConfig {
  requestCaptchaDataUrl: string;
  validCaptchaUrl: string;
  bindEl: string;
  type?: string;
  validSuccess: (
    response: Result,
    configuration: unknown,
    tac: { destroyWindow: () => void }
  ) => void;
  validFail?: (
    response: Result,
    configuration: unknown,
    tac: { destroyWindow: () => void }
  ) => void;
  btnRefreshFun?: (el: unknown, tac: unknown) => void;
  btnCloseFun?: (el: unknown, tac: unknown) => void;
  requestSuccess?: (response: unknown) => unknown;
}

export interface CaptchaStyle {
  btnUrl?: string;
  bgUrl?: string;
  logoUrl?: string | null;
  moveTrackMaskBgColor?: string;
  moveTrackMaskBorderColor?: string;
}

declare global {
  interface Window {
    initTAC: (
      path: string,
      config: CaptchaConfig,
      style?: CaptchaStyle
    ) => Promise<{ init: () => void }>;
    loadTAC: (
      path: string,
      config: CaptchaConfig,
      style?: CaptchaStyle
    ) => Promise<{ init: () => void }>;
    TAC?: unknown;
  }
}

export type { Notice } from './notice';

export interface Job {
  jobId: number;
  jobName: string;
  jobGroup: string;
  invokeTarget: string;
  cronExpression: string;
  misfirePolicy: string;
  concurrent: string;
  status: string;
  createBy?: string;
  createTime?: string;
  updateBy?: string;
  updateTime?: string;
  remark?: string;
}

export interface JobLog {
  jobLogId: number;
  jobName: string;
  jobGroup: string;
  invokeTarget: string;
  jobMessage?: string;
  status: string;
  exceptionInfo?: string;
  createTime?: string;
  stopTime?: string;
}

// 缓存监控相关类型定义
export interface CacheInfo {
  info: {
    version: string;
    mode: string;
    os: string;
    arch_bits: string;
    gcc_version: string;
    uptime_in_seconds: string;
    uptime_in_days: string;
    tcp_port: string;
    bind: string;
    connected_clients: string;
    used_memory: string;
    used_memory_human: string;
    used_memory_rss: string;
    used_memory_rss_human: string;
    used_memory_peak: string;
    used_memory_peak_human: string;
    used_memory_lua: string;
    used_memory_lua_human: string;
    maxmemory: string;
    maxmemory_human: string;
    mem_fragmentation_ratio: string;
    mem_allocator: string;
    used_cpu_sys: string;
    used_cpu_user: string;
    used_cpu_sys_children: string;
    used_cpu_user_children: string;
    aof_enabled: string;
    aof_rewrite_in_progress: string;
    rdb_last_bgsave_time_sec?: string;
    rdb_last_bgsave_status?: string;
    instantaneous_input_kbps: string;
    instantaneous_output_kbps: string;
  };
  dbSize: number;
  commandStats: CommandStat[];
}

export interface CommandStat {
  name: string;
  calls: number;
  usec: number;
  usecPerCall: number;
}

export interface CacheKeyInfo {
  key: string;
  type: string;
  size: number;
  ttl: number;
}
