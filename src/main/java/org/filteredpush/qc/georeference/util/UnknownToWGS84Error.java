package org.filteredpush.qc.georeference.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Modified from Vertnet: "Datumerrordata.js 2019-11-07T17:47:00-03:00"

@author John Wieczorek
@author mole

Copyright 2019 Rauthiflor LLC
Copyright 2023 President and Fellows of Harvard College

 */

/**
 * Utility to obtain the maximum datum shift between any unknown datum and WGS84.  
 */
public class UnknownToWGS84Error { 

	private static final Log logger = LogFactory.getLog(UnknownToWGS84Error.class);

	/**
	 * The span of each grid cell in the datum error grid in degrees.
	 */
	public static Double GRIDDEGREES = 5.0d;

	// Values of maximum datum shift between any datum and WGS84 by grid cell starting at
	// (-180, -90) and advancing by GRIDCELL degrees first in latitude, then in longitude.
	// Hence the first entry in the list is for (-180.-90), the second for (-180, -85) and 
	// the last for (175, 85). Refer to crsdiffs.csv as a reference with grid cell southwest
	// corners.
	public static int[] DATUM_ERROR_GRID = {
			3289,
			3085,
			2883,
			2684,
			2490,
			2449,
			2478,
			2505,
			2530,
			2552,
			2572,
			2589,
			2604,
			2617,
			2628,
			2637,
			2645,
			2652,
			2657,
			2663,
			2668,
			2673,
			2679,
			2685,
			2692,
			2701,
			2710,
			2720,
			2732,
			2745,
			2759,
			2773,
			2788,
			2804,
			2819,
			2834,
			3289,
			3073,
			2859,
			2647,
			2441,
			2433,
			2464,
			2495,
			2525,
			2555,
			2582,
			2609,
			2633,
			2656,
			2677,
			2696,
			2713,
			2728,
			2742,
			2754,
			2764,
			2773,
			2781,
			2787,
			2793,
			2798,
			2803,
			2807,
			2810,
			2814,
			2818,
			2823,
			2827,
			2832,
			2837,
			2843,
			3289,
			3064,
			2838,
			2615,
			2397,
			2414,
			2446,
			2479,
			2514,
			2548,
			2583,
			2617,
			2650,
			2681,
			2711,
			2739,
			2765,
			2788,
			2809,
			2827,
			2843,
			2856,
			2867,
			2874,
			2880,
			2883,
			2884,
			2883,
			2881,
			2878,
			2874,
			2869,
			2864,
			2860,
			2855,
			2851,
			3289,
			3055,
			2821,
			2588,
			2362,
			2390,
			2422,
			2457,
			2494,
			2532,
			2572,
			2612,
			2652,
			2691,
			2729,
			2765,
			2799,
			2830,
			2858,
			2883,
			2904,
			2922,
			2936,
			2946,
			2952,
			2955,
			2954,
			2950,
			2943,
			2934,
			2924,
			2912,
			2899,
			2885,
			2872,
			2860,
			3289,
			3049,
			2807,
			2566,
			2338,
			2363,
			2393,
			2427,
			2465,
			2506,
			2550,
			2595,
			2641,
			2686,
			2731,
			2774,
			2816,
			2854,
			2889,
			2921,
			2948,
			2970,
			2988,
			3002,
			3010,
			3013,
			3012,
			3007,
			2997,
			2984,
			2968,
			2950,
			2931,
			2910,
			2889,
			2868,
			3289,
			3044,
			2797,
			2549,
			2310,
			2331,
			2358,
			2390,
			2428,
			2470,
			2516,
			2564,
			2614,
			2665,
			2716,
			2766,
			2814,
			2860,
			2902,
			2940,
			2973,
			3002,
			3024,
			3042,
			3053,
			3059,
			3059,
			3053,
			3042,
			3027,
			3008,
			2985,
			2959,
			2932,
			2904,
			2876,
			3289,
			3041,
			2790,
			2538,
			2288,
			2295,
			2317,
			2346,
			2381,
			2422,
			2469,
			2519,
			2573,
			2628,
			2684,
			2740,
			2795,
			2847,
			2896,
			2941,
			2981,
			3016,
			3044,
			3066,
			3082,
			3091,
			3093,
			3089,
			3079,
			3062,
			3041,
			3015,
			2985,
			2953,
			2919,
			2883,
			3289,
			3040,
			2788,
			2534,
			2280,
			2255,
			2270,
			2294,
			2325,
			2364,
			2410,
			2461,
			2516,
			2575,
			2635,
			2696,
			2757,
			2816,
			2872,
			2924,
			2971,
			3013,
			3048,
			3076,
			3097,
			3111,
			3117,
			3115,
			3106,
			3091,
			3069,
			3041,
			3008,
			2972,
			2932,
			2890,
			3289,
			3041,
			2789,
			2535,
			2280,
			2212,
			2219,
			2235,
			2261,
			2295,
			2338,
			2388,
			2444,
			2505,
			2569,
			2635,
			2702,
			2767,
			2830,
			2890,
			2945,
			2994,
			3037,
			3072,
			3099,
			3119,
			3129,
			3132,
			3126,
			3112,
			3091,
			3062,
			3028,
			2988,
			2944,
			2897,
			3289,
			3044,
			2794,
			2542,
			2289,
			2165,
			2162,
			2169,
			2187,
			2216,
			2254,
			2302,
			2357,
			2419,
			2487,
			2557,
			2629,
			2701,
			2772,
			2839,
			2902,
			2960,
			3011,
			3054,
			3089,
			3115,
			3132,
			3139,
			3138,
			3127,
			3107,
			3080,
			3044,
			3003,
			2955,
			2903,
			3289,
			3049,
			2804,
			2555,
			2306,
			2115,
			2120,
			2115,
			2105,
			2126,
			2158,
			2202,
			2256,
			2318,
			2388,
			2462,
			2540,
			2619,
			2697,
			2773,
			2845,
			2912,
			2971,
			3023,
			3067,
			3101,
			3125,
			3139,
			3142,
			3136,
			3119,
			3093,
			3058,
			3015,
			2965,
			2909,
			3289,
			3056,
			2817,
			2575,
			2331,
			2114,
			2120,
			2112,
			2091,
			2057,
			2051,
			2089,
			2140,
			2202,
			2273,
			2352,
			2435,
			2521,
			2608,
			2693,
			2774,
			2850,
			2920,
			2982,
			3034,
			3077,
			3109,
			3130,
			3140,
			3138,
			3126,
			3102,
			3069,
			3025,
			2973,
			2914,
			3289,
			3064,
			2834,
			2600,
			2365,
			2131,
			2119,
			2110,
			2087,
			2050,
			2000,
			1964,
			2010,
			2071,
			2144,
			2226,
			2315,
			2409,
			2504,
			2599,
			2691,
			2778,
			2858,
			2930,
			2993,
			3045,
			3086,
			3115,
			3132,
			3136,
			3128,
			3108,
			3077,
			3034,
			2981,
			2918,
			3289,
			3074,
			2854,
			2631,
			2407,
			2184,
			2119,
			2108,
			2084,
			2046,
			1994,
			1929,
			1868,
			1926,
			2000,
			2087,
			2182,
			2284,
			2389,
			2494,
			2597,
			2695,
			2787,
			2870,
			2943,
			3006,
			3056,
			3094,
			3119,
			3130,
			3127,
			3111,
			3082,
			3040,
			2987,
			2922,
			3289,
			3086,
			2879,
			2668,
			2456,
			2247,
			2119,
			2108,
			2083,
			2044,
			1992,
			1926,
			1848,
			1769,
			1844,
			1935,
			2038,
			2148,
			2264,
			2380,
			2495,
			2605,
			2709,
			2804,
			2888,
			2961,
			3022,
			3069,
			3101,
			3119,
			3123,
			3111,
			3085,
			3045,
			2992,
			2925,
			3289,
			3100,
			2906,
			2710,
			2513,
			2318,
			2128,
			2108,
			2084,
			2046,
			1994,
			1929,
			1852,
			1762,
			1676,
			1772,
			1883,
			2004,
			2131,
			2260,
			2387,
			2510,
			2626,
			2733,
			2829,
			2913,
			2984,
			3040,
			3081,
			3106,
			3115,
			3109,
			3086,
			3048,
			2995,
			2928,
			3289,
			3115,
			2936,
			2756,
			2576,
			2398,
			2225,
			2110,
			2087,
			2050,
			2001,
			1938,
			1863,
			1775,
			1676,
			1600,
			1721,
			1854,
			1993,
			2135,
			2276,
			2412,
			2541,
			2660,
			2768,
			2863,
			2944,
			3009,
			3058,
			3090,
			3106,
			3104,
			3085,
			3050,
			2998,
			2930,
			3289,
			3131,
			2970,
			2807,
			2644,
			2485,
			2331,
			2184,
			2091,
			2057,
			2011,
			1952,
			1880,
			1797,
			1704,
			1599,
			1554,
			1701,
			1855,
			2011,
			2166,
			2315,
			2457,
			2588,
			2707,
			2813,
			2903,
			2977,
			3034,
			3073,
			3095,
			3098,
			3083,
			3050,
			2999,
			2932,
			3289,
			3149,
			3005,
			2861,
			2718,
			2578,
			2444,
			2317,
			2199,
			2093,
			2024,
			1970,
			1904,
			1827,
			1762,
			1735,
			1718,
			1710,
			1719,
			1890,
			2059,
			2222,
			2376,
			2519,
			2649,
			2764,
			2863,
			2945,
			3010,
			3056,
			3083,
			3090,
			3079,
			3049,
			3000,
			2933,
			3289,
			3167,
			3043,
			2919,
			2796,
			2676,
			2562,
			2455,
			2356,
			2268,
			2190,
			2123,
			2067,
			2023,
			1988,
			1963,
			1944,
			1933,
			1925,
			1921,
			1961,
			2136,
			2301,
			2455,
			2594,
			2718,
			2825,
			2915,
			2986,
			3038,
			3070,
			3082,
			3074,
			3047,
			2999,
			2933,
			3289,
			3187,
			3083,
			2979,
			2877,
			2779,
			2685,
			2598,
			2518,
			2447,
			2384,
			2330,
			2285,
			2248,
			2217,
			2193,
			2174,
			2159,
			2146,
			2136,
			2125,
			2116,
			2237,
			2399,
			2546,
			2678,
			2792,
			2887,
			2964,
			3021,
			3057,
			3073,
			3068,
			3043,
			2998,
			2932,
			3289,
			3207,
			3124,
			3042,
			2961,
			2884,
			2811,
			2744,
			2683,
			2629,
			2581,
			2539,
			2504,
			2474,
			2448,
			2426,
			2406,
			2388,
			2371,
			2353,
			2335,
			2317,
			2296,
			2353,
			2507,
			2644,
			2763,
			2863,
			2944,
			3005,
			3045,
			3064,
			3062,
			3039,
			2995,
			2932,
			3289,
			3228,
			3167,
			3106,
			3047,
			2992,
			2940,
			2892,
			2849,
			2811,
			2778,
			2749,
			2723,
			2699,
			2678,
			2658,
			2638,
			2618,
			2596,
			2573,
			2548,
			2520,
			2490,
			2457,
			2477,
			2617,
			2739,
			2843,
			2927,
			2990,
			3033,
			3054,
			3055,
			3034,
			2992,
			2930,
			3289,
			3249,
			3210,
			3171,
			3134,
			3100,
			3069,
			3041,
			3016,
			2994,
			2974,
			2957,
			2940,
			2924,
			2907,
			2889,
			2869,
			2847,
			2821,
			2793,
			2760,
			2724,
			2685,
			2641,
			2595,
			2599,
			2723,
			2828,
			2913,
			2978,
			3022,
			3045,
			3047,
			3028,
			2988,
			2928,
			3289,
			3271,
			3254,
			3237,
			3222,
			3209,
			3198,
			3189,
			3182,
			3175,
			3169,
			3163,
			3155,
			3145,
			3133,
			3117,
			3098,
			3074,
			3045,
			3011,
			2972,
			2928,
			2879,
			2825,
			2767,
			2705,
			2713,
			2817,
			2902,
			2967,
			3012,
			3036,
			3039,
			3022,
			2984,
			2926,
			3289,
			3293,
			3297,
			3303,
			3310,
			3317,
			3326,
			3336,
			3345,
			3353,
			3360,
			3365,
			3366,
			3363,
			3355,
			3342,
			3322,
			3297,
			3264,
			3225,
			3180,
			3128,
			3070,
			3007,
			2938,
			2864,
			2785,
			2811,
			2895,
			2958,
			3003,
			3027,
			3031,
			3014,
			2978,
			2923,
			3289,
			3314,
			3341,
			3368,
			3396,
			3424,
			3452,
			3479,
			3505,
			3528,
			3547,
			3562,
			3571,
			3575,
			3571,
			3560,
			3541,
			3514,
			3479,
			3435,
			3384,
			3324,
			3258,
			3184,
			3104,
			3019,
			2928,
			2832,
			2890,
			2952,
			2994,
			3018,
			3022,
			3006,
			2972,
			2919,
			3289,
			3336,
			3384,
			3432,
			3481,
			3528,
			3575,
			3619,
			3660,
			3697,
			3728,
			3753,
			3770,
			3780,
			3780,
			3771,
			3753,
			3725,
			3687,
			3639,
			3581,
			3515,
			3440,
			3357,
			3266,
			3169,
			3066,
			2958,
			2889,
			2947,
			2987,
			3009,
			3012,
			2998,
			2965,
			2915,
			3289,
			3357,
			3426,
			3495,
			3563,
			3630,
			3694,
			3755,
			3810,
			3860,
			3902,
			3936,
			3961,
			3977,
			3981,
			3974,
			3956,
			3927,
			3886,
			3834,
			3771,
			3698,
			3614,
			3522,
			3422,
			3314,
			3199,
			3079,
			2953,
			2943,
			2980,
			3000,
			3003,
			2988,
			2957,
			2910,
			3289,
			3377,
			3466,
			3555,
			3642,
			3727,
			3808,
			3884,
			3954,
			4015,
			4068,
			4111,
			4144,
			4164,
			4172,
			4168,
			4150,
			4119,
			4076,
			4020,
			3951,
			3871,
			3781,
			3680,
			3570,
			3451,
			3325,
			3193,
			3055,
			2940,
			2973,
			2990,
			2992,
			2978,
			2949,
			2905,
			3289,
			3397,
			3505,
			3613,
			3718,
			3820,
			3917,
			4007,
			4090,
			4163,
			4226,
			4277,
			4315,
			4341,
			4352,
			4350,
			4332,
			4301,
			4255,
			4195,
			4121,
			4035,
			3937,
			3828,
			3708,
			3580,
			3444,
			3300,
			3150,
			2996,
			2965,
			2980,
			2980,
			2967,
			2940,
			2900,
			3289,
			3416,
			3543,
			3668,
			3790,
			3908,
			4019,
			4123,
			4217,
			4301,
			4373,
			4431,
			4476,
			4506,
			4521,
			4520,
			4502,
			4470,
			4421,
			4357,
			4279,
			4187,
			4082,
			3965,
			3837,
			3699,
			3553,
			3399,
			3238,
			3072,
			2957,
			2969,
			2968,
			2955,
			2930,
			2894,
			3289,
			3434,
			3578,
			3719,
			3857,
			3990,
			4115,
			4231,
			4336,
			4429,
			4509,
			4575,
			4625,
			4659,
			4676,
			4676,
			4659,
			4625,
			4574,
			4507,
			4424,
			4326,
			4215,
			4090,
			3954,
			3808,
			3652,
			3489,
			3318,
			3142,
			2961,
			2956,
			2954,
			2942,
			2920,
			2888,
			3289,
			3450,
			3610,
			3767,
			3920,
			4065,
			4203,
			4330,
			4445,
			4547,
			4634,
			4705,
			4760,
			4798,
			4817,
			4818,
			4801,
			4766,
			4712,
			4642,
			4554,
			4451,
			4334,
			4203,
			4059,
			3905,
			3741,
			3568,
			3389,
			3203,
			3013,
			2942,
			2939,
			2928,
			2909,
			2882,
			3289,
			3465,
			3640,
			3811,
			3977,
			4134,
			4283,
			4419,
			4543,
			4653,
			4746,
			4823,
			4882,
			4922,
			4943,
			4945,
			4927,
			4891,
			4835,
			4761,
			4670,
			4562,
			4439,
			4301,
			4151,
			3990,
			3818,
			3637,
			3449,
			3256,
			3057,
			2926,
			2923,
			2913,
			2897,
			2875,
			3289,
			3479,
			3668,
			3851,
			4028,
			4196,
			4354,
			4499,
			4631,
			4747,
			4846,
			4927,
			4989,
			5031,
			5054,
			5056,
			5038,
			4999,
			4941,
			4864,
			4769,
			4657,
			4528,
			4385,
			4229,
			4061,
			3882,
			3695,
			3500,
			3299,
			3093,
			2908,
			2905,
			2897,
			2884,
			2868,
			3289,
			3492,
			3692,
			3886,
			4073,
			4251,
			4416,
			4569,
			4707,
			4828,
			4931,
			5016,
			5080,
			5125,
			5148,
			5150,
			5131,
			5091,
			5030,
			4950,
			4852,
			4735,
			4602,
			4454,
			4293,
			4119,
			3934,
			3741,
			3540,
			3332,
			3121,
			2906,
			2885,
			2879,
			2871,
			2860,
			3289,
			3503,
			3713,
			3916,
			4112,
			4297,
			4470,
			4628,
			4771,
			4896,
			5003,
			5090,
			5156,
			5202,
			5225,
			5226,
			5206,
			5164,
			5102,
			5019,
			4917,
			4796,
			4659,
			4507,
			4341,
			4162,
			3973,
			3774,
			3568,
			3356,
			3140,
			2920,
			2863,
			2861,
			2857,
			2853,
			3289,
			3512,
			3730,
			3942,
			4144,
			4335,
			4513,
			4677,
			4823,
			4951,
			5060,
			5149,
			5216,
			5262,
			5285,
			5285,
			5264,
			5220,
			5155,
			5069,
			4964,
			4840,
			4700,
			4544,
			4373,
			4191,
			3997,
			3795,
			3585,
			3369,
			3150,
			2927,
			2839,
			2841,
			2843,
			2845,
			3289,
			3520,
			3745,
			3962,
			4170,
			4366,
			4547,
			4714,
			4863,
			4993,
			5103,
			5192,
			5260,
			5305,
			5327,
			5326,
			5303,
			5257,
			5189,
			5101,
			4993,
			4866,
			4723,
			4563,
			4390,
			4204,
			4008,
			3803,
			3590,
			3372,
			3150,
			2926,
			2813,
			2820,
			2828,
			2837,
			3289,
			3525,
			3756,
			3977,
			4189,
			4387,
			4572,
			4740,
			4890,
			5021,
			5131,
			5220,
			5287,
			5331,
			5351,
			5349,
			5323,
			5275,
			5205,
			5114,
			5004,
			4874,
			4728,
			4566,
			4390,
			4202,
			4004,
			3797,
			3583,
			3364,
			3142,
			2918,
			2785,
			2798,
			2812,
			2829,
			3289,
			3529,
			3763,
			3987,
			4201,
			4401,
			4586,
			4754,
			4904,
			5035,
			5145,
			5232,
			5297,
			5339,
			5358,
			5353,
			5325,
			5275,
			5202,
			5109,
			4996,
			4864,
			4716,
			4552,
			4375,
			4185,
			3986,
			3778,
			3564,
			3346,
			3124,
			2901,
			2755,
			2775,
			2797,
			2821,
			3289,
			3531,
			3766,
			3992,
			4206,
			4406,
			4590,
			4758,
			4907,
			5035,
			5143,
			5229,
			5291,
			5331,
			5347,
			5339,
			5309,
			5256,
			5181,
			5085,
			4970,
			4836,
			4686,
			4521,
			4343,
			4153,
			3954,
			3747,
			3533,
			3316,
			3097,
			2877,
			2724,
			2751,
			2781,
			2813,
			3289,
			3532,
			3767,
			3991,
			4204,
			4402,
			4585,
			4750,
			4896,
			5022,
			5127,
			5210,
			5269,
			5306,
			5318,
			5308,
			5274,
			5218,
			5141,
			5043,
			4926,
			4791,
			4639,
			4474,
			4295,
			4106,
			3907,
			3702,
			3491,
			3276,
			3060,
			2844,
			2691,
			2726,
			2765,
			2806,
			3289,
			3530,
			3763,
			3985,
			4195,
			4390,
			4570,
			4731,
			4874,
			4996,
			5097,
			5176,
			5231,
			5264,
			5273,
			5259,
			5222,
			5163,
			5083,
			4983,
			4864,
			4728,
			4576,
			4410,
			4232,
			4043,
			3847,
			3644,
			3436,
			3226,
			3015,
			2805,
			2657,
			2701,
			2749,
			2798,
			3289,
			3527,
			3756,
			3974,
			4180,
			4371,
			4545,
			4702,
			4840,
			4957,
			5053,
			5127,
			5178,
			5206,
			5211,
			5193,
			5152,
			5090,
			5008,
			4905,
			4785,
			4647,
			4495,
			4330,
			4153,
			3967,
			3773,
			3574,
			3370,
			3166,
			2961,
			2757,
			2622,
			2676,
			2733,
			2791,
			3289,
			3522,
			3745,
			3958,
			4158,
			4343,
			4511,
			4662,
			4794,
			4905,
			4996,
			5064,
			5109,
			5132,
			5133,
			5110,
			5066,
			5001,
			4915,
			4811,
			4689,
			4551,
			4399,
			4235,
			4060,
			3876,
			3686,
			3491,
			3294,
			3095,
			2898,
			2703,
			2586,
			2651,
			2718,
			2784,
			3289,
			3515,
			3732,
			3937,
			4130,
			4307,
			4469,
			4613,
			4737,
			4842,
			4925,
			4987,
			5027,
			5044,
			5039,
			5012,
			4964,
			4895,
			4807,
			4701,
			4578,
			4439,
			4288,
			4125,
			3952,
			3772,
			3587,
			3397,
			3206,
			3016,
			2827,
			2642,
			2551,
			2627,
			2703,
			2777,
			3289,
			3506,
			3715,
			3911,
			4095,
			4265,
			4418,
			4553,
			4670,
			4767,
			4843,
			4898,
			4931,
			4942,
			4932,
			4900,
			4847,
			4775,
			4684,
			4576,
			4451,
			4313,
			4162,
			4001,
			3832,
			3656,
			3475,
			3292,
			3109,
			2927,
			2748,
			2575,
			2516,
			2603,
			2689,
			2771,
			3289,
			3496,
			3694,
			3881,
			4055,
			4215,
			4359,
			4485,
			4593,
			4682,
			4750,
			4797,
			4823,
			4827,
			4811,
			4774,
			4717,
			4641,
			4547,
			4437,
			4311,
			4173,
			4024,
			3865,
			3699,
			3527,
			3353,
			3177,
			3003,
			2830,
			2663,
			2501,
			2483,
			2581,
			2676,
			2765,
			3289,
			3485,
			3671,
			3847,
			4010,
			4159,
			4292,
			4409,
			4507,
			4586,
			4645,
			4684,
			4703,
			4701,
			4678,
			4635,
			4574,
			4494,
			4397,
			4285,
			4159,
			4021,
			3872,
			3716,
			3554,
			3388,
			3220,
			3053,
			2887,
			2726,
			2570,
			2422,
			2451,
			2560,
			2663,
			2760,
			3289,
			3471,
			3645,
			3808,
			3960,
			4097,
			4219,
			4324,
			4412,
			4482,
			4532,
			4562,
			4572,
			4563,
			4534,
			4485,
			4419,
			4335,
			4235,
			4121,
			3995,
			3857,
			3710,
			3557,
			3399,
			3239,
			3078,
			2919,
			2764,
			2615,
			2472,
			2338,
			2422,
			2541,
			2653,
			2756,
			3289,
			3457,
			3617,
			3766,
			3904,
			4029,
			4139,
			4233,
			4310,
			4369,
			4409,
			4430,
			4433,
			4416,
			4380,
			4326,
			4254,
			4166,
			4064,
			3948,
			3820,
			3683,
			3538,
			3388,
			3235,
			3081,
			2928,
			2778,
			2634,
			2497,
			2368,
			2261,
			2396,
			2524,
			2643,
			2752,
			3289,
			3441,
			3586,
			3721,
			3845,
			3956,
			4053,
			4135,
			4200,
			4249,
			4279,
			4291,
			4285,
			4260,
			4217,
			4157,
			4081,
			3989,
			3884,
			3766,
			3638,
			3501,
			3358,
			3211,
			3063,
			2915,
			2770,
			2630,
			2497,
			2373,
			2274,
			2251,
			2373,
			2510,
			2636,
			2749,
			3289,
			3424,
			3552,
			3672,
			3781,
			3878,
			3962,
			4031,
			4085,
			4122,
			4142,
			4145,
			4130,
			4097,
			4048,
			3982,
			3900,
			3805,
			3696,
			3576,
			3448,
			3312,
			3171,
			3027,
			2884,
			2743,
			2606,
			2476,
			2356,
			2279,
			2272,
			2247,
			2355,
			2499,
			2630,
			2747,
			3289,
			3406,
			3517,
			3621,
			3714,
			3796,
			3866,
			3923,
			3964,
			3990,
			3999,
			3992,
			3969,
			3929,
			3872,
			3800,
			3714,
			3614,
			3503,
			3382,
			3252,
			3117,
			2978,
			2838,
			2699,
			2565,
			2437,
			2317,
			2272,
			2279,
			2268,
			2242,
			2342,
			2491,
			2626,
			2745,
			3289,
			3387,
			3481,
			3567,
			3644,
			3711,
			3767,
			3810,
			3839,
			3853,
			3852,
			3836,
			3804,
			3756,
			3693,
			3615,
			3524,
			3420,
			3306,
			3183,
			3053,
			2918,
			2781,
			2644,
			2511,
			2383,
			2263,
			2255,
			2274,
			2277,
			2264,
			2235,
			2334,
			2487,
			2624,
			2745,
			3289,
			3368,
			3442,
			3511,
			3571,
			3623,
			3664,
			3693,
			3710,
			3713,
			3702,
			3676,
			3635,
			3580,
			3510,
			3427,
			3331,
			3224,
			3107,
			2982,
			2851,
			2717,
			2582,
			2448,
			2319,
			2198,
			2232,
			2261,
			2275,
			2274,
			2257,
			2227,
			2331,
			2486,
			2624,
			2745,
			3289,
			3348,
			3403,
			3453,
			3497,
			3532,
			3558,
			3574,
			3578,
			3570,
			3548,
			3513,
			3465,
			3402,
			3326,
			3238,
			3138,
			3027,
			2908,
			2781,
			2650,
			2516,
			2382,
			2251,
			2158,
			2207,
			2244,
			2266,
			2274,
			2269,
			2249,
			2216,
			2334,
			2488,
			2626,
			2746,
			3289,
			3327,
			3363,
			3394,
			3420,
			3439,
			3451,
			3453,
			3444,
			3425,
			3394,
			3350,
			3293,
			3224,
			3143,
			3050,
			2946,
			2832,
			2710,
			2582,
			2449,
			2315,
			2183,
			2132,
			2184,
			2225,
			2253,
			2269,
			2272,
			2261,
			2239,
			2204,
			2343,
			2495,
			2630,
			2748,
			3289,
			3306,
			3322,
			3334,
			3343,
			3345,
			3342,
			3330,
			3310,
			3279,
			3238,
			3186,
			3122,
			3047,
			2961,
			2863,
			2756,
			2639,
			2515,
			2385,
			2252,
			2118,
			2113,
			2165,
			2208,
			2240,
			2260,
			2269,
			2266,
			2252,
			2226,
			2195,
			2357,
			2505,
			2637,
			2751,
			3289,
			3285,
			3280,
			3274,
			3264,
			3251,
			3232,
			3207,
			3174,
			3133,
			3083,
			3023,
			2953,
			2872,
			2781,
			2680,
			2569,
			2451,
			2325,
			2194,
			2060,
			2101,
			2152,
			2194,
			2227,
			2250,
			2263,
			2266,
			2257,
			2238,
			2210,
			2222,
			2377,
			2518,
			2645,
			2755,
			3289,
			3264,
			3239,
			3213,
			3186,
			3156,
			3122,
			3083,
			3039,
			2988,
			2929,
			2862,
			2786,
			2700,
			2606,
			2501,
			2389,
			2268,
			2142,
			2046,
			2097,
			2144,
			2184,
			2217,
			2241,
			2256,
			2262,
			2258,
			2244,
			2222,
			2190,
			2256,
			2401,
			2535,
			2655,
			2760,
			3289,
			3243,
			3198,
			3153,
			3107,
			3061,
			3012,
			2961,
			2905,
			2844,
			2777,
			2703,
			2622,
			2533,
			2435,
			2329,
			2215,
			2094,
			2054,
			2100,
			2142,
			2179,
			2210,
			2233,
			2249,
			2256,
			2255,
			2245,
			2227,
			2201,
			2168,
			2296,
			2430,
			2555,
			2667,
			2765,
			3289,
			3222,
			3157,
			3093,
			3030,
			2967,
			2903,
			2839,
			2772,
			2702,
			2628,
			2548,
			2462,
			2370,
			2270,
			2206,
			2156,
			2099,
			2107,
			2144,
			2177,
			2205,
			2227,
			2242,
			2250,
			2250,
			2242,
			2227,
			2204,
			2176,
			2212,
			2341,
			2463,
			2577,
			2680,
			2771,
			3289,
			3202,
			3117,
			3034,
			2953,
			2874,
			2796,
			2719,
			2641,
			2562,
			2481,
			2396,
			2321,
			2293,
			2257,
			2213,
			2163,
			2116,
			2149,
			2178,
			2202,
			2222,
			2235,
			2242,
			2242,
			2236,
			2222,
			2202,
			2176,
			2160,
			2276,
			2390,
			2500,
			2602,
			2695,
			2777,
			3289,
			3182,
			3078,
			2977,
			2878,
			2783,
			2691,
			2601,
			2513,
			2425,
			2362,
			2342,
			2321,
			2293,
			2257,
			2214,
			2165,
			2154,
			2179,
			2200,
			2217,
			2228,
			2233,
			2233,
			2226,
			2213,
			2195,
			2171,
			2147,
			2245,
			2345,
			2443,
			2538,
			2628,
			2711,
			2784,
			3289,
			3163,
			3041,
			2921,
			2805,
			2694,
			2588,
			2486,
			2466,
			2441,
			2407,
			2365,
			2317,
			2287,
			2252,
			2209,
			2159,
			2180,
			2198,
			2211,
			2220,
			2223,
			2221,
			2214,
			2200,
			2182,
			2159,
			2176,
			2252,
			2333,
			2415,
			2498,
			2579,
			2656,
			2728,
			2792,
			3289,
			3145,
			3005,
			2867,
			2735,
			2609,
			2491,
			2493,
			2487,
			2472,
			2450,
			2422,
			2388,
			2350,
			2310,
			2269,
			2229,
			2193,
			2204,
			2210,
			2211,
			2207,
			2198,
			2184,
			2165,
			2197,
			2243,
			2296,
			2356,
			2420,
			2487,
			2554,
			2621,
			2685,
			2745,
			2800,
			3289,
			3129,
			2971,
			2817,
			2668,
			2526,
			2493,
			2502,
			2505,
			2500,
			2490,
			2474,
			2454,
			2430,
			2405,
			2378,
			2352,
			2328,
			2307,
			2291,
			2280,
			2277,
			2280,
			2292,
			2311,
			2338,
			2372,
			2412,
			2457,
			2506,
			2558,
			2611,
			2664,
			2715,
			2763,
			2808,
			3289,
			3113,
			2939,
			2769,
			2605,
			2470,
			2491,
			2508,
			2518,
			2524,
			2524,
			2521,
			2513,
			2503,
			2491,
			2477,
			2463,
			2450,
			2439,
			2430,
			2425,
			2424,
			2428,
			2437,
			2451,
			2470,
			2494,
			2522,
			2555,
			2590,
			2628,
			2667,
			2706,
			2745,
			2782,
			2817,
			3289,
			3098,
			2909,
			2724,
			2545,
			2461,
			2487,
			2509,
			2527,
			2541,
			2552,
			2560,
			2564,
			2566,
			2566,
			2564,
			2561,
			2558,
			2556,
			2554,
			2555,
			2557,
			2561,
			2568,
			2578,
			2591,
			2607,
			2625,
			2647,
			2670,
			2695,
			2721,
			2748,
			2774,
			2801,
			2825
	};

	/** Obtain coordinate uncertainty in meters for the given latitude and longitude that results
	 * from the difference between an unknown datum and WGS84 at the specified point.
	 * 
	 * @param decimalLatitude for the coordinate to check
	 * @param decimalLongitude for the coordinate to check
	 * @return an integer value of coordinate uncertainty in meters due to the unknown datum 
	 */
	public static Integer getErrorAtCoordinate(double decimalLatitude, double decimalLongitude) { 
		Integer result = null;
		int position = 0;
		if (decimalLatitude==90d) { decimalLatitude = 89d; }
		if (decimalLongitude==180d) { decimalLongitude = 179d; } 
		int latitudeOffset = ((Double)(Math.floor((90d+decimalLatitude)/GRIDDEGREES))).intValue();
		int longitudeOffset = ((Double)(Math.floor((180d+decimalLongitude)/GRIDDEGREES))).intValue();

		position = latitudeOffset + (longitudeOffset*36);

		result = DATUM_ERROR_GRID[position];

		return result;
	}

}