# ellipsoid-quantile

Functions in python and Java, which calculate ellipsoid quantile the way Mathematica does. It should work well on 2D and higher dimensional points.

## How to use it?
Functions in both python and Java take two parameters: `data` and `quantile`. Data should be a matrix with sample per row, quantile should be a number between 0 and 1.

### python
code is in ellipsoid_quantile.ipynb, numpy is required.

data might be a numpy array or anything compatible enough.
* `mean` - mean of ellipse
* `radii` - radii of the ellipse (in 2D case height and width of ellipse)
* `eig_vects` - eigenvectors of ellipse (to calculate the rotation of the ellipse)

## Java
Code is in EllipsoidQuantile/src/com/company/EllipsoidQuantile.java, [EJML library](http://ejml.org/) is required. The code is messy and can be optimised nevertheless it's been tested and gives reasonable results.

`data` is EJML's SimpleMatrix, `quantile` is a double

Array of Objects is returned.
* `mean` - mean of ellipse (`SimpleMatrix`)
* `radii` - radii of the ellipse (in 2D case height and width of ellipse) (`double[ ]`)
* `eig_vects` - eigenvectors of ellipse (to calculate the rotation of the ellipse) (`SimpleMatrix[ ]`)

## How does it work?
1. Estimate covariance, inversed covariance matrix and mean of the data. Covariance's eigen vectors need to be calculated as well.
2. Transform data to a space in which ellipsoid will be a sphere with mean in zero and for each data point calculate its distance from zero. $sqrt((x-mean).T \Sigma^(-1) (x-mean))$. We need to know how many data points are at particular distance from zero.
3. Calculating qlevel. In this spherical space that we're now in imagine a set of smaller spheres such that for each of these smaller spheres there exists a transformed data point which lays exactly on this sphere (the radius of this smaller sphere is equal to the point's distance from the mean). For each smaller sphere calculate how many transformed data points are contained by this smaller sphere.
4. Finally calculate how big should be the sphere that includes exactly `quantile` data points:

  4a. Having the transfomed points and their distances calculate between which two points this "quantile sphere" should go (`distance[index_inner], distance[index_outer]` in the code)
  
  4b. 
    
    * `do` is the distance of the first (closest to zero) transformed point in the data that should be outside the "quantile sphere"
      
    * `qo` - what quantile would we have to calculate for the "quantile sphere" to go exactly through the point mentioned above
    
    * `di` is the distance of the last (furthest from zero) transformed point that should lay within the "quantile sphere"
    
    * `qi` - same as above
    
    * `q` - the quantile we want to calculate
    
    Use equation $di + (do-di)*((q-qi)/(qo-qi))$

5. Go back to original space.
