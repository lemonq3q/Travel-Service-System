<template>
  <div class="map-selector-container">
    <div class="search-box">
      <el-input
        v-model="searchKey"
        id="search-input"
        placeholder="搜索地点、地址"
        clearable
        @clear="handleSearchClear"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>
    <div id="map-container" class="map-container"></div>
    <div class="info-box" v-if="selectedPos.lng !== null && selectedPos.lng !== undefined">
      当前选中：经度 {{ selectedPos.lng }}, 纬度 {{ selectedPos.lat }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import AMapLoader from '@amap/amap-jsapi-loader';
import { defineExpose } from 'vue';

// 外部调用更新坐标 + 居中地图
const setCoordinates = (lng, lat) => {
  lng = lng ?? DEFAULT_LNG;
  lat = lat ?? DEFAULT_LAT;
  if (!map || !marker) return;
  // 更新标记和数据
  updateMarker(lng, lat);
  // 强制地图居中
  map.setCenter([lng, lat]);
};

// 暴露方法给父组件
defineExpose({
  setCoordinates
});

const DEFAULT_LNG = 116.397428;
const DEFAULT_LAT = 39.90923;

const props = defineProps({
  longitude: {
    type: Number,
    default: 116.397428
  },
  latitude: {
    type: Number,
    default: 39.90923
  }
});

const emit = defineEmits(['update:coordinates']);

const searchKey = ref('');
const selectedPos = ref({
  lng: props.longitude ?? DEFAULT_LNG,
  lat: props.latitude ?? DEFAULT_LAT
});

let map = null;
let marker = null;
let autoComplete = null;
let placeSearch = null;

window._AMapSecurityConfig = {
  securityJsCode: 'd2cf19f99afe609da0cd05d3f2294e51',
};

const initMap = () => {
  AMapLoader.load({
    key: '3f2c976216af0c59f0ee1e32c72aea06',
    version: '2.0',
    plugins: ['AMap.AutoComplete', 'AMap.PlaceSearch', 'AMap.Geocoder'],
  }).then((AMap) => {
    const initLng = props.longitude ?? DEFAULT_LNG;
    const initLat = props.latitude ?? DEFAULT_LAT;
    map = new AMap.Map('map-container', {
      viewMode: '3D',
      zoom: 17,
      center: [initLng, initLat],
    });

    // 初始化标记
    marker = new AMap.Marker({
      position: [initLng, initLat],
      map: map,
    });

    selectedPos.value = { lng: initLng, lat: initLat };

    if (props.longitude === null || props.longitude === undefined || props.latitude === null || props.latitude === undefined) {
      emit('update:coordinates', { lng: initLng, lat: initLat });
    }

    // 点击地图选点
    map.on('click', (e) => {
      const lng = e.lnglat.getLng();
      const lat = e.lnglat.getLat();
      updateMarker(lng, lat);
    });

    // 搜索功能
    autoComplete = new AMap.AutoComplete({
      input: 'search-input'
    });
    placeSearch = new AMap.PlaceSearch({
      map: map
    });

    autoComplete.on('select', (e) => {
      placeSearch.setCity(e.poi.adcode);
      placeSearch.search(e.poi.name, (status, result) => {
        if (status === 'complete' && result.poiList.pois.length > 0) {
          const poi = result.poiList.pois[0];
          updateMarker(poi.location.lng, poi.location.lat);
        }
      });
    });

  }).catch(e => {
    console.error('地图加载失败', e);
  });
};

const updateMarker = (lng, lat, emitChange = true) => {
  selectedPos.value = { lng, lat };
  if (marker) {
    marker.setPosition([lng, lat]);
  }
  // 设置地图中心点
  // if (map) {
  //   map.setCenter([lng, lat]);
  // }
  if (emitChange) {
    emit('update:coordinates', { lng, lat });
  }
};

const handleSearch = () => {
  if (placeSearch && searchKey.value) {
    placeSearch.search(searchKey.value, (status, result) => {
      if (status === 'complete' && result.poiList.pois.length > 0) {
        const poi = result.poiList.pois[0];
        updateMarker(poi.location.lng, poi.location.lat);
      }
    });
  }
};

const handleSearchClear = () => {
  searchKey.value = '';
};

watch(
  () => [props.longitude, props.latitude],
  ([lng, lat]) => {
    const nextLng = lng ?? DEFAULT_LNG;
    const nextLat = lat ?? DEFAULT_LAT;

    if (lng === null || lng === undefined || lat === null || lat === undefined) {
      emit('update:coordinates', { lng: nextLng, lat: nextLat });
    }

    if (!map) return;
    if (selectedPos.value.lng === nextLng && selectedPos.value.lat === nextLat) return;
    updateMarker(nextLng, nextLat, false);
  },
  { immediate: true }
);

onMounted(() => {
  initMap();
});

onUnmounted(() => {
  if (map) {
    map.destroy();
  }
});
</script>

<style scoped>
.map-selector-container {
  width: 100%;
  position: relative;
}
.search-box {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 100;
  width: 300px;
}
.map-container {
  width: 100%;
  height: 400px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.info-box {
  margin-top: 10px;
  font-size: 12px;
  color: #666;
}
</style>
