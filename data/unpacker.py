import matplotlib.pyplot as plt
import numpy as np
import xarray as xr
from PIL import Image

# To avoid warning messages
import warnings
warnings.filterwarnings('ignore')

# Open the dataset
DS = xr.open_dataset("BIO01_access1-0_rcp45_r1i1p1_1950-2100_v1.0.nc")

# Print information about the dataset
print(DS)

for i in range(1950, 2101):

    # Subset the data if needed
    subset = DS.sel(time=f"{i}")

    # Extract the variable
    bT = subset['BIO01']

    print(bT.time)

    bT.rio.set_spatial_dims(x_dim='longitude', y_dim='latitude')
    bT.rio.write_crs("epsg:4326", inplace=True)
    bT.plot(add_colorbar=False)
    plt.axis('off')
    plt.title('')
    # plt.legend().remove()
    plt.savefig(f"{i}.png", bbox_inches="tight", pad_inches=0, dpi=300)
    plt.close()

    image_path = f"{i}.png"
    Image.open(image_path).convert('RGB').save(image_path)